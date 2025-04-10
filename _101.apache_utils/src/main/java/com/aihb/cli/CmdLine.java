package com.aihb.cli;

import org.apache.commons.cli.*;
import org.junit.Test;

public class CmdLine {

    Options options = new Options();

    @Test
    public void TestOptions() throws ParseException {

        // agentname -n 必要参数
        Option option = new Option("n", "name", true, "the name of this agent");
        option.setRequired(true);
        options.addOption(option);

        // 配置文件 -f 必要参数
        option = new Option("f", "conf-file", true,
                "specify a config file (required if -z missing)");
        option.setRequired(false);
        options.addOption(option);

        option = new Option(null, "no-reload-conf", false,
                "do not reload config file if changed");
        options.addOption(option);

        // Options for Zookeeper
        option = new Option("z", "zkConnString", true,
                "specify the ZooKeeper connection to use (required if -f missing)");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("p", "zkBasePath", true,
                "specify the base path in ZooKeeper for agent configs");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("h", "help", false, "display help text");
        options.addOption(option);


        System.out.println(options);


        String [] args = {  "-f", "conf/flume.conf", "--name", "agentname", "-h" };

        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options, args);


        /*Option[] options = commandLine.getOptions();
        for (Option option1 : options) {
            System.out.println(option1.toString());
        }*/


        if (commandLine.hasOption('h')) {
            new HelpFormatter().printHelp("flume-ng agent", options, true);
            return;
        }

    }

}
