/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.service;

public class UserContext {

    private static final ThreadLocal<String> currentUserDatabase = new ThreadLocal<>();

    public static void setCurrentUserDatabase(String dbName) {
        currentUserDatabase.set(dbName);
    }

    public static String getCurrentUserDatabase() {
        return currentUserDatabase.get();
    }

    public static void clear() {
        currentUserDatabase.remove();
    }
}

