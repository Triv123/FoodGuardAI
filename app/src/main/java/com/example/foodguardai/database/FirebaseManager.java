package com.example.foodguardai.database;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.foodguardai.models.DietEntry;
import com.example.foodguardai.models.ScanHistory;
import com.example.foodguardai.models.User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // ===== USER AUTHENTICATION =====

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    public void signUp(String email, String password, String name, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Create user document
                        User user = new User(firebaseUser.getUid(), email, name);
                        createUserDocument(user, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Sign up failed", e);
                    callback.onError(e.getMessage());
                });
    }

    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    callback.onSuccess(authResult.getUser());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Sign in failed", e);
                    callback.onError(e.getMessage());
                });
    }

    public void signOut() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    private void createUserDocument(User user, AuthCallback callback) {
        db.collection("users")
                .document(user.getUid())
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document created");
                    callback.onSuccess(auth.getCurrentUser());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create user document", e);
                    callback.onError(e.getMessage());
                });
    }

    // ===== DIET TRACKING =====

    public interface DietCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface DietEntriesCallback {
        void onSuccess(List<DietEntry> entries);
        void onError(String error);
    }

    public void addDietEntry(DietEntry entry, DietCallback callback) {
        String userId = getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("dietEntries")
                .add(entry.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Diet entry added: " + documentReference.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add diet entry", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getTodaysDietEntries(DietEntriesCallback callback) {
        String userId = getCurrentUser().getUid();
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.US).format(new java.util.Date());

        db.collection("users")
                .document(userId)
                .collection("dietEntries")
                .whereEqualTo("date", today)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DietEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DietEntry entry = document.toObject(DietEntry.class);
                        entry.setId(document.getId());
                        entries.add(entry);
                    }
                    callback.onSuccess(entries);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get diet entries", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getDietEntriesByDate(String date, DietEntriesCallback callback) {
        String userId = getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("dietEntries")
                .whereEqualTo("date", date)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DietEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DietEntry entry = document.toObject(DietEntry.class);
                        entry.setId(document.getId());
                        entries.add(entry);
                    }
                    callback.onSuccess(entries);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ===== SCAN HISTORY =====

    public void addScanHistory(ScanHistory scan, DietCallback callback) {
        String userId = getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("scanHistory")
                .add(scan.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Scan history added");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add scan history", e);
                    callback.onError(e.getMessage());
                });
    }

    public interface ScanHistoryCallback {
        void onSuccess(List<ScanHistory> scans);
        void onError(String error);
    }

    public void getScanHistory(int limit, ScanHistoryCallback callback) {
        String userId = getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("scanHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ScanHistory> scans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ScanHistory scan = document.toObject(ScanHistory.class);
                        scan.setId(document.getId());
                        scans.add(scan);
                    }
                    callback.onSuccess(scans);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}