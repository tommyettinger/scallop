package com.github.tommyettinger.headless;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.github.tommyettinger.AtlasScaler;
import com.github.tommyettinger.Scallop;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Launches the desktop (headless) application. */
public class PaletteTest {
	public static void main(String[] args) throws IOException {
		AtomicInteger runCount = new AtomicInteger(0);
		HeadlessApplicationConfiguration config = getDefaultConfiguration();
		Path orig = Paths.get("headless/src/test/resources/Eye_Tyrant.png");
		new File("bw").mkdir();
		new File("gs").mkdir();
		Files.copy(orig, Paths.get("bw/Eye_Tyrant.png"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(orig, Paths.get("gs/Eye_Tyrant.png"), StandardCopyOption.REPLACE_EXISTING);
		new HeadlessApplication(new Scallop("-000000 -FFFFFF bw/Eye_Tyrant.png".split(" "), runCount), config);
		new HeadlessApplication(new Scallop("-0x081820FF -0x346856FF -0x88C070FF -0xE0F8D0FF gs/Eye_Tyrant.png".split(" "), runCount), config);
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		return configuration;
	}
}
