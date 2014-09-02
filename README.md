# External Tasks Runner -- Maven Plugin

## Description

This plugin allows you to easily bind an external task to a given Maven phase.

For now it has been used only with *grunt* and *gulp* but it should work with any tasks runner.

## Usage

Add a plugin section to your pom.xml :

	<build>
		<plugins>
              <plugin>
                    <groupId>com.worldline.maven.plugin</groupId>
                    <artifactId>external-tasks-runner</artifactId>
                    <version>0.5.1</version>
                    <configuration>
                        <taskRunnerName>grunt</taskRunnerName>
                    </configuration>
                    <executions>
                          <execution>
                                <id>init</id>
                                <goals> <goal>init</goal> </goals>
                          </execution>
                          <execution>
                                <id>run-test</id>
                                <goals>
                                    <goal>run</goal>
                                </goals>
                                <phase>test</phase>
                                <configuration>
                                    <task>my-grunt-task</task>
                                    <test>true</test>
                                </configuration>
                          </execution>
                    </executions>
              </plugin>
		</plugins>
	</build>

This snippet will run the grunt task "my-grunt-task" when the test phase is reached.


## Unit testing

To get full unit testing support (including Jenkins reports) you need to use
a xunit compatible reporter with your unit testing (mochajs has one).
For instance you could have one task named test in your gruntfile which corresponds
to local unit testing with any fancy reporters you'd like to use, and another task
called test-xunit using the xunit reporter and which is called by your pom.xml on the
test phase.

This task should write its output in a xml file in a path known by your Jenkins instance.

This plugin supports the following properties to skip test phase: `skipTests` and `maven.test.skip`

# Copyright

(c) 2014, Worldline By Atos

Written by :
 - Frédéric Langlade-Bellone <frederic.langlade@worldline.net>
 - Adrien Plagnol <adrien.plagnol@worldline.net>


