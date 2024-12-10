package com.vcs.Commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import com.vcs.Utils.StagingArea;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "commit", description = "Create a new commit object")
public class Commit implements Runnable {

    StagingArea stagingArea = new StagingArea();
    private String OBJECTS_DIR = ".vcs/objects";

    @Option(names = "-m", description = "Commit message")
    private String commitMessage;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            commit(commitMessage);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private void commit(String message) throws IOException, NoSuchAlgorithmException {
        // Check if there are any staged files
        if (stagingArea.getStagedFiles().isEmpty()) {
            System.out.println("No changes to commit");
            return;
        }

        // Create commit
        createCommit(stagingArea);

        // Clear staging area after commit
        stagingArea.clear();

    }

    public void createCommit(StagingArea stagingArea) throws IOException, NoSuchAlgorithmException {
        // Create tree object from staged files
        String treeHash = CreateTree.writeTreeFromStagedFiles();

        // Read the last hash commit from HEAD
        String lastCommitHash = null;
        if (Files.exists(Paths.get(".vcs/HEAD"))) {
            lastCommitHash = new String(Files.readAllBytes(Paths.get(".vcs/HEAD"))).trim();
        }

        // Hash and store commit object
        CommitTree.commitTreeCommand(treeHash, lastCommitHash, commitMessage);
    }

}