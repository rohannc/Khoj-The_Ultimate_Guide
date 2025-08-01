package com.rohan.Khoj.entity;

public enum AffiliationStatus {
    PENDING,  // The initial state when a request is sent, awaiting a response.
    APPROVED, // The final state when the request has been accepted by the recipient.
    REJECTED  // The final state when the request has been declined by the recipient.
}