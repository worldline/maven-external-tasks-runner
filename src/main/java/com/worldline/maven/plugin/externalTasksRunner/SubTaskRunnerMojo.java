package com.worldline.maven.plugin.externalTasksRunner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;


@Mojo( name = "run", defaultPhase = LifecyclePhase.INITIALIZE )
public class SubTaskRunnerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${os.name}", readonly = true)
    String osName;

    @Parameter(required = false)
    String taskRunnerPath;

    @Parameter(required = true)
    String taskRunnerName;

    @Parameter(required = true)
    String task;

    @Parameter(defaultValue = " ", required = false)
    String additionalParameters;

    @Parameter(defaultValue = "false", required = false)
    Boolean isTestPhase;

    @Parameter(defaultValue = "true", required = false)
    Boolean failOnError;

    @Parameter(property = "maven.test.failure.ignore", defaultValue = "false")
    Boolean ignoreTestsResults;

    /** Base working directory.
     *
     * @parameter default-value="${project.basedir}"
     * @required
     * @readonly
     */
	@Parameter(property = "external-tasks-maven.basedir", defaultValue = "${project.basedir}")
	protected File basedir;

    @Parameter(property = "skipTests", defaultValue = "false")
    protected Boolean isSkippingTests;

    @Parameter(property = "maven.test.skip", defaultValue = "false")
    protected Boolean isSkippingTestsAlt;


    private org.apache.maven.project.MavenProject mavenProject;

    public void execute() throws MojoExecutionException {
        if (isTestPhase && (isSkippingTests || isSkippingTestsAlt)) {
            getLog().info("skipping tests");
            return;
        }
        String params = "";
        if ( System.getProperty("env") != null ) {
            params += "--env=" + System.getProperty("env");
        }
        params += " " + defaultParamsFor(taskRunnerName) + " " + additionalParameters;

        if (taskRunnerPath == null) {
            taskRunnerPath = taskRunnerName;
        }

        executeCommand( taskRunnerPath + " " + task + " " + params, task );
    }

    private String defaultParamsFor(String taskRunnerName) {
        if (taskRunnerName == "grunt") {
            return "--no-color";
        }

        return "";
    }

    void executeCommand( String command, String taskName ) throws MojoExecutionException {
        try {
            if (isWindows()) {
                command = "cmd /c " + command;
            }

            getLog().debug( "Running command : "  + command );
            CommandLine cmdLine = CommandLine.parse( command );
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(basedir);
            executor.execute( cmdLine );
        } catch (ExecuteException e) {
            getLog().error( "Task not found or failure : " + taskName );

            if (isTestPhase && ignoreTestsResults) {
                getLog().warn("ignoring tests result");
            } else if (!failOnError) {
                getLog().warn("Task failed but we keep going on");
            } else {
                throw new MojoExecutionException( "Task not found or failure : " + taskName );
            }

        } catch (IOException e) {
            throw new MojoExecutionException( "Unknown error when running", e );
        }


    }


    private boolean isWindows() {
        return osName.startsWith("Windows");
    }
}
