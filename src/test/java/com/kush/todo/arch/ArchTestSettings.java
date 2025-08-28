package com.kush.todo.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ArchTestSettings {

    @SuppressWarnings("PMD.LooseCoupling")
    public static final JavaClasses JAVA_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.kush.todo");

    private ArchTestSettings() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String buildExcludeList(Collection<String> collection) {
        return collection.stream()
                         .map(Pattern::quote)
                         .collect(Collectors.joining("|"));
    }
}