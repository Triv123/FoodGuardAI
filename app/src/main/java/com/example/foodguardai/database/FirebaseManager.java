package com.example.foodguardai.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

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
                    } else {
                        callback.onError("User creation failed: FirebaseUser is null");
                    }
                })
                .addOnFailureListener(e -> {
                    String userMessage = mapAuthError(e);
                    Log.e(TAG, "Sign up failed: " + e.getMessage(), e);
                    callback.onError(userMessage);
                });
    }

    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        callback.onSuccess(authResult.getUser());
                    } else {
                        callback.onError("Sign in failed: FirebaseUser is null");
                    }
                })
                .addOnFailureListener(e -> {
                    String userMessage = mapAuthError(e);
                    Log.e(TAG, "Sign in failed: " + e.getMessage(), e);
                    callback.onError(userMessage);
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

    private String mapAuthError(Exception e) {
        if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            Log.w(TAG, "FirebaseAuth error code: " + errorCode);
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "The email address is badly formatted.";
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "This email address is already in use.";
                case "ERROR_WEAK_PASSWORD":
                    return "Password is too weak. Please choose a stronger password.";
                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "Email/password sign-in is disabled in Firebase Console.";
                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Network error. Please check your internet connection and try again.";
                default:
                    return "Authentication failed. (" + errorCode + ")";
            }
        }
        return "Authentication failed. " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
    }

    private void createUserDocument(User user, AuthCallback callback) {
        db.collection("users")
                .document(user.getUid())
                .set(user.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document created successfully");
                    callback.onSuccess(auth.getCurrentUser());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create user document: " + e.getMessage(), e);
                    callback.onError("Account created but profile setup failed: " + e.getMessage());
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
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("dietEntries")
                .add(entry.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Diet entry added: " + documentReference.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add diet entry: " + e.getMessage(), e);
                    callback.onError(e.getMessage());
                });
    }

    public void getTodaysDietEntries(DietEntriesCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }

        String today = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.US).format(new java.util.Date());

        db.collection("users")
                .document(user.getUid())
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
                    Log.e(TAG, "Failed to get diet entries: " + e.getMessage(), e);
                    callback.onError(e.getMessage());
                });
    }

    public void getDietEntriesByDate(String date, DietEntriesCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection("users")
                .document(user.getUid())
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
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("scanHistory")
                .add(scan.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Scan history added successfully");
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add scan history: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public interface ScanHistoryCallback {
        void onSuccess(List<ScanHistory> scans);
        void onError(String error);
    }

    public void getScanHistory(int limit, ScanHistoryCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onError("User not authenticated");
            return;
        }

        db.collection("users")
                .document(user.getUid())
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