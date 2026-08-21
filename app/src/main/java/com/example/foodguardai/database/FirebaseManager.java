//package com.example.foodguardai.database;
//
//import android.util.Log;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.example.foodguardai.models.DietEntry;
//import com.example.foodguardai.models.ScanHistory;
//import com.example.foodguardai.models.User;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FirebaseManager {
//    private static final String TAG = "FirebaseManager";
//
//    private static FirebaseManager instance;
//    private final FirebaseAuth auth;
//    private final FirebaseFirestore db;
//
//    private FirebaseManager() {
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//    }
//
//    public static synchronized FirebaseManager getInstance() {
//        if (instance == null) {
//            instance = new FirebaseManager();
//        }
//        return instance;
//    }
//
//    // ===== USER AUTHENTICATION =====
//
//    public interface AuthCallback {
//        void onSuccess(FirebaseUser user);
//        void onError(String error);
//    }
//
//    public void signUp(String email, String password, String name, AuthCallback callback) {
//        auth.createUserWithEmailAndPassword(email, password)
//                .addOnSuccessListener(authResult -> {
//                    FirebaseUser firebaseUser = authResult.getUser();
//                    if (firebaseUser != null) {
//                        // Create user document
//                        User user = new User(firebaseUser.getUid(), email, name);
//                        createUserDocument(user, callback);
//                    } else {
//                        callback.onError("User creation failed: FirebaseUser is null");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    String userMessage = mapAuthError(e);
//                    Log.e(TAG, "Sign up failed: " + e.getMessage(), e);
//                    callback.onError(userMessage);
//                });
//    }
//
//    public void signIn(String email, String password, AuthCallback callback) {
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnSuccessListener(authResult -> {
//                    if (authResult.getUser() != null) {
//                        callback.onSuccess(authResult.getUser());
//                    } else {
//                        callback.onError("Sign in failed: FirebaseUser is null");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    String userMessage = mapAuthError(e);
//                    Log.e(TAG, "Sign in failed: " + e.getMessage(), e);
//                    callback.onError(userMessage);
//                });
//    }
//
//    public void signOut() {
//        auth.signOut();
//    }
//
//    public FirebaseUser getCurrentUser() {
//        return auth.getCurrentUser();
//    }
//
//    public boolean isUserLoggedIn() {
//        return auth.getCurrentUser() != null;
//    }
//
//    private String mapAuthError(Exception e) {
//        if (e instanceof FirebaseAuthException) {
//            String errorCode = ((FirebaseAuthException) e).getErrorCode();
//            Log.w(TAG, "FirebaseAuth error code: " + errorCode);
//            switch (errorCode) {
//                case "ERROR_INVALID_EMAIL":
//                    return "The email address is badly formatted.";
//                case "ERROR_EMAIL_ALREADY_IN_USE":
//                    return "This email address is already in use.";
//                case "ERROR_WEAK_PASSWORD":
//                    return "Password is too weak. Please choose a stronger password.";
//                case "ERROR_OPERATION_NOT_ALLOWED":
//                    return "Email/password sign-in is disabled in Firebase Console.";
//                case "ERROR_NETWORK_REQUEST_FAILED":
//                    return "Network error. Please check your internet connection and try again.";
//                default:
//                    return "Authentication failed. (" + errorCode + ")";
//            }
//        }
//        return "Authentication failed. " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
//    }
//
//    private void createUserDocument(User user, AuthCallback callback) {
//        db.collection("users")
//                .document(user.getUid())
//                .set(user.toMap())
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "User document created successfully");
//                    callback.onSuccess(auth.getCurrentUser());
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to create user document: " + e.getMessage(), e);
//                    callback.onError("Account created but profile setup failed: " + e.getMessage());
//                });
//    }
//
//    // ===== DIET TRACKING =====
//
//    public interface DietCallback {
//        void onSuccess();
//        void onError(String error);
//    }
//
//    public interface DietEntriesCallback {
//        void onSuccess(List<DietEntry> entries);
//        void onError(String error);
//    }
//
//    public void addDietEntry(DietEntry entry, DietCallback callback) {
//        FirebaseUser user = getCurrentUser();
//        if (user == null) {
//            callback.onError("User not authenticated");
//            return;
//        }
//
//        db.collection("users")
//                .document(user.getUid())
//                .collection("dietEntries")
//                .add(entry.toMap())
//                .addOnSuccessListener(documentReference -> {
//                    Log.d(TAG, "Diet entry added: " + documentReference.getId());
//                    callback.onSuccess();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to add diet entry: " + e.getMessage(), e);
//                    callback.onError(e.getMessage());
//                });
//    }
//
//    public void getTodaysDietEntries(DietEntriesCallback callback) {
//        FirebaseUser user = getCurrentUser();
//        if (user == null) {
//            callback.onError("User not authenticated");
//            return;
//        }
//
//        String today = new java.text.SimpleDateFormat("yyyy-MM-dd",
//                java.util.Locale.US).format(new java.util.Date());
//
//        db.collection("users")
//                .document(user.getUid())
//                .collection("dietEntries")
//                .whereEqualTo("date", today)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<DietEntry> entries = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        DietEntry entry = document.toObject(DietEntry.class);
//                        entry.setId(document.getId());
//                        entries.add(entry);
//                    }
//                    callback.onSuccess(entries);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to get diet entries: " + e.getMessage(), e);
//                    callback.onError(e.getMessage());
//                });
//    }
//
//    public void getDietEntriesByDate(String date, DietEntriesCallback callback) {
//        FirebaseUser user = getCurrentUser();
//        if (user == null) {
//            callback.onError("User not authenticated");
//            return;
//        }
//
//        db.collection("users")
//                .document(user.getUid())
//                .collection("dietEntries")
//                .whereEqualTo("date", date)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<DietEntry> entries = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        DietEntry entry = document.toObject(DietEntry.class);
//                        entry.setId(document.getId());
//                        entries.add(entry);
//                    }
//                    callback.onSuccess(entries);
//                })
//                .addOnFailureListener(e -> callback.onError(e.getMessage()));
//    }
//
//    // ===== SCAN HISTORY =====
//
//    public void addScanHistory(ScanHistory scan, DietCallback callback) {
//        FirebaseUser user = getCurrentUser();
//        if (user == null) {
//            if (callback != null) callback.onError("User not authenticated");
//            return;
//        }
//
//        db.collection("users")
//                .document(user.getUid())
//                .collection("scanHistory")
//                .add(scan.toMap())
//                .addOnSuccessListener(documentReference -> {
//                    Log.d(TAG, "Scan history added successfully");
//                    if (callback != null) callback.onSuccess();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Failed to add scan history: " + e.getMessage(), e);
//                    if (callback != null) callback.onError(e.getMessage());
//                });
//    }
//
//    public interface ScanHistoryCallback {
//        void onSuccess(List<ScanHistory> scans);
//        void onError(String error);
//    }
//
//    public void getScanHistory(int limit, ScanHistoryCallback callback) {
//        FirebaseUser user = getCurrentUser();
//        if (user == null) {
//            callback.onError("User not authenticated");
//            return;
//        }
//
//        db.collection("users")
//                .document(user.getUid())
//                .collection("scanHistory")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .limit(limit)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<ScanHistory> scans = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        ScanHistory scan = document.toObject(ScanHistory.class);
//                        scan.setId(document.getId());
//                        scans.add(scan);
//                    }
//                    callback.onSuccess(scans);
//                })
//                .addOnFailureListener(e -> callback.onError(e.getMessage()));
//    }
//}

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
import com.example.foodguardai.models.SusResponse;
import com.example.foodguardai.models.FeatureUsageLog;
import com.example.foodguardai.models.ExpertRating;
import com.example.foodguardai.models.AlgorithmScore;

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
                        User user = new User(firebaseUser.getUid(), email, name);
                        createUserDocument(user, callback);
                    } else {
                        callback.onError("User creation failed");
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
                        callback.onError("Sign in failed");
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

    // BUG FIX: Updated error codes to match current Firebase SDK format
    private String mapAuthError(Exception e) {
        if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            Log.w(TAG, "FirebaseAuth error code: " + errorCode);
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "The email address is badly formatted.";
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "This email is already registered. Please login instead.";
                case "ERROR_WEAK_PASSWORD":
                    return "Password is too weak. Use at least 6 characters.";
                case "ERROR_WRONG_PASSWORD":
                    return "Incorrect password. Please try again.";
                case "ERROR_USER_NOT_FOUND":
                    return "No account found with this email.";
                case "ERROR_USER_DISABLED":
                    return "This account has been disabled.";
                case "ERROR_TOO_MANY_REQUESTS":
                    return "Too many failed attempts. Please try again later.";
                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "Email/password sign-in is disabled. Please contact support.";
                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Network error. Please check your internet connection.";
                case "ERROR_INVALID_CREDENTIAL":
                    return "Invalid email or password. Please check and try again.";
                default:
                    return "Authentication failed. (" + errorCode + ")";
            }
        }
        // Fallback for non-FirebaseAuthException errors
        String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
        if (msg.contains("CONFIGURATION_NOT_FOUND")) {
            return "Firebase not configured. Please check your setup.";
        }
        if (msg.contains("NETWORK_ERROR") || msg.contains("network")) {
            return "Network error. Please check your internet connection.";
        }
        return "Authentication failed: " + msg;
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
                    // BUG FIX: Auth succeeded, so still call onSuccess even if Firestore fails
                    // The user is authenticated; profile data failure shouldn't block them
                    callback.onSuccess(auth.getCurrentUser());
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

        // BUG FIX: Removed compound query that requires Firestore index.
        // Filter by date only, then sort in memory to avoid index errors.
        db.collection("users")
                .document(user.getUid())
                .collection("dietEntries")
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DietEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DietEntry entry = document.toObject(DietEntry.class);
                        entry.setId(document.getId());
                        entries.add(entry);
                    }
                    // Sort by timestamp descending in memory
                    entries.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
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

        // BUG FIX: Same fix - avoid compound query needing index
        db.collection("users")
                .document(user.getUid())
                .collection("dietEntries")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DietEntry> entries = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DietEntry entry = document.toObject(DietEntry.class);
                        entry.setId(document.getId());
                        entries.add(entry);
                    }
                    entries.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
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

        // BUG FIX: Use simple query without compound orderBy to avoid index requirement
        db.collection("users")
                .document(user.getUid())
                .collection("scanHistory")
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ScanHistory> scans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ScanHistory scan = document.toObject(ScanHistory.class);
                        scan.setId(document.getId());
                        scans.add(scan);
                    }
                    // Sort by timestamp descending in memory
                    scans.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                    callback.onSuccess(scans);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ===== SUS RESPONSES =====

    public interface SusCallback {
        void onSuccess();
        void onError(String error);
    }

    public void saveSusResponse(SusResponse response, SusCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }

        db.collection("susResponses")
                .add(response.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "SUS response saved: " + documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save SUS response: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void getSusResponses(String participantId, SusEntriesCallback callback) {
        db.collection("susResponses")
                .whereEqualTo("participantId", participantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SusResponse> responses = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        SusResponse response = document.toObject(SusResponse.class);
                        response.setId(document.getId());
                        responses.add(response);
                    }
                    callback.onSuccess(responses);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface SusEntriesCallback {
        void onSuccess(List<SusResponse> responses);
        void onError(String error);
    }

    // ===== FEATURE USAGE LOGGING =====

    public void saveFeatureUsage(FeatureUsageLog usage, DietCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            if (callback != null) callback.onError("User not authenticated");
            return;
        }

        db.collection("featureUsage")
                .add(usage.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Feature usage saved: " + documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save feature usage: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    // ===== EXPERT RATINGS =====

    public void saveExpertRating(ExpertRating rating, DietCallback callback) {
        db.collection("expertRatings")
                .add(rating.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Expert rating saved: " + documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save expert rating: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void getExpertRatings(String productId, ExpertRatingsCallback callback) {
        db.collection("expertRatings")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ExpertRating> ratings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ExpertRating rating = document.toObject(ExpertRating.class);
                        rating.setId(document.getId());
                        ratings.add(rating);
                    }
                    callback.onSuccess(ratings);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface ExpertRatingsCallback {
        void onSuccess(List<ExpertRating> ratings);
        void onError(String error);
    }

    // ===== ALGORITHM SCORES =====

    public void saveAlgorithmScore(AlgorithmScore score, DietCallback callback) {
        db.collection("algorithmScores")
                .add(score.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Algorithm score saved: " + documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save algorithm score: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void getAlgorithmScores(String productId, AlgorithmScoresCallback callback) {
        db.collection("algorithmScores")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AlgorithmScore> scores = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        AlgorithmScore score = document.toObject(AlgorithmScore.class);
                        score.setId(document.getId());
                        scores.add(score);
                    }
                    callback.onSuccess(scores);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface AlgorithmScoresCallback {
        void onSuccess(List<AlgorithmScore> scores);
        void onError(String error);
    }

    // ===== TEST LOGS =====

    public void saveTestLog(com.example.foodguardai.logging.TestLog log, DietCallback callback) {
        db.collection("testLogs")
                .add(log.toMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Test log saved: " + documentReference.getId());
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save test log: " + e.getMessage(), e);
                    if (callback != null) callback.onError(e.getMessage());
                });
    }
}