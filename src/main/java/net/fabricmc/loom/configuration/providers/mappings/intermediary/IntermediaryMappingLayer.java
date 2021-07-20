/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.configuration.providers.mappings.intermediary;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

import net.fabricmc.loom.configuration.providers.mappings.MappingLayer;
import net.fabricmc.loom.configuration.providers.mappings.MappingNamespace;
import net.fabricmc.mappingio.MappingVisitor;
import net.fabricmc.mappingio.adapter.MappingNsCompleter;
import net.fabricmc.mappingio.format.Tiny2Reader;

public class IntermediaryMappingLayer implements MappingLayer {
	private final Supplier<File> tinyFile;

	public IntermediaryMappingLayer(Supplier<File> tinyFile) {
		this.tinyFile = tinyFile;
	}

	public File tinyFile() {
		return tinyFile.get();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		IntermediaryMappingLayer that = (IntermediaryMappingLayer) obj;
		return Objects.equals(this.tinyFile, that.tinyFile);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tinyFile);
	}

	@Override
	public String toString() {
		return "IntermediaryMappingLayer[tinyFile=" + tinyFile + ']';
	}

	@Override
	public MappingNamespace getSourceNamespace() {
		return MappingNamespace.OFFICIAL;
	}

	@Override
	public void visit(MappingVisitor mappingVisitor) throws IOException {
		// Populate named with intermediary and add Add a "named" namespace
		MappingNsCompleter nsCompleter = new MappingNsCompleter(mappingVisitor, Collections.singletonMap(MappingNamespace.NAMED.stringValue(), MappingNamespace.INTERMEDIARY.stringValue()), true);

		try (BufferedReader reader = Files.newBufferedReader(tinyFile().toPath(), StandardCharsets.UTF_8)) {
			Tiny2Reader.read(reader, nsCompleter);
		}
	}
}
