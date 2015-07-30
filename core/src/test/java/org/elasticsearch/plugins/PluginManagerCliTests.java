/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.plugins;

import com.google.common.base.Strings;
import org.elasticsearch.common.cli.CliToolTestCase;
import org.elasticsearch.common.io.Streams;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.cli.CliTool.ExitStatus.OK;
import static org.elasticsearch.common.cli.CliTool.ExitStatus.OK_AND_EXIT;
import static org.hamcrest.Matchers.*;

public class PluginManagerCliTests extends CliToolTestCase {

    @Test
    public void testHelpWorks() throws IOException {
        CliToolTestCase.CaptureOutputTerminal terminal = new CliToolTestCase.CaptureOutputTerminal();
        assertThat(new PluginManagerCliParser(terminal).execute(args("--help")), is(OK_AND_EXIT));
        assertHelp(terminal, "/org/elasticsearch/plugins/plugin.help");

        terminal.getTerminalOutput().clear();
        assertThat(new PluginManagerCliParser(terminal).execute(args("install -h")), is(OK_AND_EXIT));
        assertHelp(terminal, "/org/elasticsearch/plugins/plugin-install.help");
        for (String plugin : PluginManager.OFFICIAL_PLUGINS) {
            assertThat(terminal.getTerminalOutput(), hasItem(containsString(plugin)));
        }

        terminal.getTerminalOutput().clear();
        assertThat(new PluginManagerCliParser(terminal).execute(args("remove --help")), is(OK_AND_EXIT));
        assertHelp(terminal, "/org/elasticsearch/plugins/plugin-remove.help");

        terminal.getTerminalOutput().clear();
        assertThat(new PluginManagerCliParser(terminal).execute(args("list -h")), is(OK_AND_EXIT));
        assertHelp(terminal, "/org/elasticsearch/plugins/plugin-list.help");
    }

    private void assertHelp(CliToolTestCase.CaptureOutputTerminal terminal, String classPath) throws IOException {
        List<String> nonEmptyLines = new ArrayList<>();
        for (String line : terminal.getTerminalOutput()) {
            String originalPrintedLine = line.replaceAll(System.lineSeparator(), "");
            if (Strings.isNullOrEmpty(originalPrintedLine)) {
                nonEmptyLines.add(originalPrintedLine);
            }
        }
        assertThat(nonEmptyLines, hasSize(greaterThan(0)));

        String expectedDocs = Streams.copyToStringFromClasspath(classPath);
        for (String nonEmptyLine : nonEmptyLines) {
            assertThat(expectedDocs, containsString(nonEmptyLine.replaceAll(System.lineSeparator(), "")));
        }
    }
}