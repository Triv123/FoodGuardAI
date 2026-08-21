package com.example.foodguardai;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodguardai.adapters.ChatAdapter;
import com.example.foodguardai.api.ApiManager;
import com.example.foodguardai.logging.TestLog;
import com.example.foodguardai.logging.TestLogManager;
import com.example.foodguardai.models.ChatMessage;
import com.example.foodguardai.utils.VoiceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class AIChatActivity extends AppCompatActivity {

    // Replace with your actual API key
    private static final int PERMISSION_RECORD_AUDIO = 200;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private LinearProgressIndicator progressBar;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private String productName;
    private int safetyRating;

    private VoiceManager voiceManager;
    private MaterialButton btnVoiceInput;
    private boolean isListening = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat);
        voiceManager = new VoiceManager(this);
        productName = getIntent().getStringExtra("PRODUCT_NAME");
        safetyRating = getIntent().getIntExtra("SAFETY_RATING", 0);

        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_RECORD_AUDIO);
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupVoiceButton();

        // Send initial context message
        sendInitialMessage();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupVoiceButton() {
        btnVoiceInput = findViewById(R.id.btnVoiceInput);

        btnVoiceInput.setOnClickListener(v -> {
            if (isListening) {
                stopVoiceInput();
            } else {
                startVoiceInput();
            }
        });
    }

    private void startVoiceInput() {
        voiceManager.startListening(new VoiceManager.VoiceCommandListener() {
            @Override
            public void onCommandReceived(String command) {
                runOnUiThread(() -> {
                    etMessage.setText(command);
                    isListening = false;
                    btnVoiceInput.setIcon(ContextCompat.getDrawable(AIChatActivity.this, R.drawable.ic_mic));

                    // Auto-send the command
                    sendMessage(command);
                });
            }

            @Override
            public void onListeningStarted() {
                runOnUiThread(() -> {
                    isListening = true;
                    btnVoiceInput.setIcon(ContextCompat.getDrawable(AIChatActivity.this, R.drawable.ic_mic_listening));
                    Toast.makeText(AIChatActivity.this,
                            "Listening... Speak now", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onListeningEnded() {
                runOnUiThread(() -> {
                    isListening = false;
                    btnVoiceInput.setIcon(ContextCompat.getDrawable(AIChatActivity.this, R.drawable.ic_mic));
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    isListening = false;
                    btnVoiceInput.setIcon(ContextCompat.getDrawable(AIChatActivity.this, R.drawable.ic_mic));
                    Toast.makeText(AIChatActivity.this,
                            "Voice error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void stopVoiceInput() {
        voiceManager.stopListening();
        isListening = false;
        btnVoiceInput.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_mic));
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                etMessage.setText("");
            }
        });
    }

    private void sendInitialMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
                "Hello! I'm your AI food safety assistant. I can help you understand more about " +
                        productName + " (Safety Rating: " + safetyRating + "/10). What would you like to know?",
                false
        );
        messages.add(welcomeMessage);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void sendMessage(String userMessage) {
        // Add user message to chat
        ChatMessage userMsg = new ChatMessage(userMessage, true);
        messages.add(userMsg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        // Prepare product context
        String productContext = "Product: " + productName + ", Safety Rating: " + safetyRating + "/10";
        long aiRequestStart = System.currentTimeMillis();

        // Send to API
        ApiManager.sendChatMessage(userMessage, productContext, new ApiManager.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                long latency = System.currentTimeMillis() - aiRequestStart;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    voiceManager.speak(response);
                    ChatMessage aiMsg = new ChatMessage(response, false);
                    messages.add(aiMsg);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    scrollToBottom();
                });

                TestLog aiChatLog = TestLogManager.getInstance().createAiLog(
                        "success", latency, "product_chat",
                        productContext, null,
                        String.valueOf(messages.size()),
                        response, "openrouter");
                TestLogManager.getInstance().log(aiChatLog);
            }

            @Override
            public void onError(String error) {
                long latency = System.currentTimeMillis() - aiRequestStart;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    showErrorMessage(error);
                });

                TestLog aiChatLog = TestLogManager.getInstance().createAiLog(
                        "error", latency, "product_chat",
                        productContext, null,
                        String.valueOf(messages.size()),
                        error, "openrouter");
                TestLogManager.getInstance().log(aiChatLog);
            }
        });
    }

    private void showErrorMessage(String error) {
        ChatMessage errorMsg = new ChatMessage(
                "Sorry, I encountered an error. Please try again. Error: " + error,
                false
        );
        messages.add(errorMsg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            recyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (voiceManager != null) {
            voiceManager.cleanup();
        }
    }
}