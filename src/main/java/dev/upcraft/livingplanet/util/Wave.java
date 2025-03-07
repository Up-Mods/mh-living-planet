package dev.upcraft.livingplanet.util;

public record Wave(long timeStarted, float angle) {
    public static final int LIFETIME = 40;
    public static final double LENGTH = 30;
}
