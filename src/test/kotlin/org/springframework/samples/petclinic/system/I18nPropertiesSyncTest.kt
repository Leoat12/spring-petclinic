package org.springframework.samples.petclinic.system

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap
import java.util.Properties
import java.util.TreeSet
import java.util.regex.Pattern
import java.util.stream.Stream
import kotlin.io.path.readLines

class I18nPropertiesSyncTest {

    companion object {
        private const val I18N_DIR = "src/main/resources"
        private const val BASE_NAME = "messages"
        const val PROPERTIES = ".properties"
        private val HTML_TEXT_LITERAL = Pattern.compile(">([^<>{}]+)<")
        private val BRACKET_ONLY = Pattern.compile("<[^>]*>\\s*[\\[\\]](?:&nbsp;)?\\s*</[^>]*>")
        private val HAS_TH_TEXT_ATTRIBUTE = Pattern.compile("th:(u)?text\\s*=\\s*\"[^\"]+\"")
    }

    @Test
    fun checkNonInternationalizedStrings() {
        val root = Path.of("src/main")
        val files: List<Path>

        try {
            Files.walk(root).use { stream ->
                files = stream
                    .filter { p -> p.toString().endsWith(".java") || p.toString().endsWith(".html") || p.toString().endsWith(".kt") }
                    .filter { p -> !p.toString().contains("/test/") }
                    .filter { p -> !(p.getFileName()?.toString()?.endsWith("Test.java") ?: false) && !(p.getFileName()?.toString()?.endsWith("Tests.kt") ?: false) }
                    .toList()
            }
        } catch (e: Exception) {
            throw e
        }

        val report = StringBuilder()

        for (file in files) {
            val lines = file.readLines()
            for (i in lines.indices) {
                val line = lines[i].trim()

                if (line.startsWith("//") || line.startsWith("@") || line.contains("log.") || line.contains("System.out")) {
                    continue
                }

                if (file.toString().endsWith(".html")) {
                    val hasLiteralText = HTML_TEXT_LITERAL.matcher(line).find()
                    val hasThTextAttribute = HAS_TH_TEXT_ATTRIBUTE.matcher(line).find()
                    val isBracketOnly = BRACKET_ONLY.matcher(line).find()

                    if (hasLiteralText && !line.contains("#{") && !hasThTextAttribute && !isBracketOnly) {
                        report.append("HTML: ")
                            .append(file)
                            .append(" Line ")
                            .append(i + 1)
                            .append(": ")
                            .append(line)
                            .append("\n")
                    }
                }
            }
        }

        if (report.isNotEmpty()) {
            fail<Nothing>("Hardcoded (non-internationalized) strings found:\n$report")
        }
    }

    @Test
    fun checkI18nPropertyFilesAreInSync() {
        val propertyFiles: List<Path>
        try {
            Files.walk(Path.of(I18N_DIR)).use { stream ->
                propertyFiles = stream
                    .filter { p -> p.getFileName()?.toString()?.startsWith(BASE_NAME) ?: false }
                    .filter { p -> p.getFileName()?.toString()?.endsWith(PROPERTIES) ?: false }
                    .toList()
            }
        } catch (e: Exception) {
            throw e
        }

        val localeToProps = HashMap<String, Properties>()

        for (path in propertyFiles) {
            val props = Properties()
            Files.newBufferedReader(path).use { reader ->
                props.load(reader)
                localeToProps[path.getFileName()?.toString() ?: "unknown"] = props
            }
        }

        val baseFile = BASE_NAME + PROPERTIES
        val baseProps = localeToProps[baseFile]
        if (baseProps == null) {
            fail<Nothing>("Base properties file '$baseFile' not found.")
            return
        }

        val baseKeys = baseProps.stringPropertyNames()
        val report = StringBuilder()

        for ((fileName, props) in localeToProps) {
            if (fileName == baseFile || fileName == "messages_en.properties") {
                continue
            }

            val missingKeys = TreeSet(baseKeys)
            missingKeys.removeAll(props.stringPropertyNames())

            if (missingKeys.isNotEmpty()) {
                report.append("Missing keys in $fileName:\n")
                missingKeys.forEach { k -> report.append("  $k\n") }
            }
        }

        if (report.isNotEmpty()) {
            fail<Nothing>("Translation files are not in sync:\n$report")
        }
    }

}