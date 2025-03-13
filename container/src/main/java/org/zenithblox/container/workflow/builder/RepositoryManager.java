package org.zenithblox.container.workflow.builder;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.zenithblox.ZwangineContext;
import org.zenithblox.container.configs.WorkflowContextManager;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResourceHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepositoryManager {

    private static String REPOSITORY_NAME="zwangine_repository";
    private static String REPOSITORY_PATH = System.getProperty("user.home") + File.separator + REPOSITORY_NAME;
    @Qualifier("simple-task-executor")
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    @PostConstruct
    @Async
    public void init() throws Exception {
        ZwangineContext context = WorkflowContextManager.getContext();
        simpleAsyncTaskExecutor.execute(() -> {
            File directory = new File(REPOSITORY_PATH);
            if (!directory.exists()) {
                directory.mkdir();
            }

            try(WatchService watchService = FileSystems.getDefault().newWatchService()){
                try {
                    Paths.get(REPOSITORY_PATH).register(watchService ,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);
                    loadModeOnStartup(context);
                    processChange(watchService, context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    private void loadModeOnStartup(ZwangineContext context) {
        Path path = Paths.get(REPOSITORY_PATH);
        try (Stream<Path> files = Files.list(path)) {
            files.forEach(file -> {
                log.info("Loading workflow {} from repository", file.getFileName().toString());
                try {
                    String workflow = new String(Files.readAllBytes(file));
                    PluginHelper.getWorkflowsLoader(context).loadWorkflows(ResourceHelper.fromString(Paths.get(REPOSITORY_PATH).resolve(file).toString(), workflow.strip()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadWorkflow(ZwangineContext context, String[] modelContent, Path repository, Path filePath ) throws Exception {
        Arrays.stream(modelContent).forEach(it->{
            try {
                PluginHelper.getWorkflowsLoader(context).loadWorkflows(ResourceHelper.fromString(repository.resolve(filePath).toString(), it.strip()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void loadWorkflow(ZwangineContext context, String modelContent, Path filePath) throws Exception {
        PluginHelper.getWorkflowsLoader(context).loadWorkflows(ResourceHelper.fromString(Paths.get(REPOSITORY_PATH).resolve(filePath).toString(), modelContent.strip()));
    }



    private String[] processWorkflowMeta(Path directoryPath, Path filePath) throws IOException, InterruptedException {
        log.info("extension {}", getFileExtension(filePath).equals("yaml"));
        if(!getFileExtension(filePath).equals("yaml") && !getFileExtension(filePath).equals("zwg")){
            log.info("Processing workflow file {}", directoryPath.resolve(filePath));
            Files.delete(directoryPath.resolve(filePath));
            return null;
        }
        return new String[]{new String(Files.readAllBytes(filePath))};
    }

    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf(".");

        // Vérifier si un point est présent et que ce n'est pas le premier caractère
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return ""; // Retourne une chaîne vide si pas d'extension
    }



    private void  processChange(WatchService watchService, ZwangineContext context) throws Exception {
        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path filePath = (Path) event.context();
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    log.info("Workflow file added : " + filePath);
                    String[] modelContent =  processWorkflowMeta(Paths.get(REPOSITORY_PATH), filePath);
                    if(modelContent != null){
                        loadWorkflow(context , modelContent , Paths.get(REPOSITORY_PATH), filePath);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    log.info("Workflow file Modified : " + filePath);
                    String[] modelContent =  processWorkflowMeta(Paths.get(REPOSITORY_PATH), filePath);
                    if(modelContent != null){
                        loadWorkflow(context , modelContent , Paths.get(REPOSITORY_PATH), filePath);
                    }
                    processWorkflowMeta(Paths.get(REPOSITORY_PATH), filePath);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    log.info("Workflow file deleted : " + filePath);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                log.error("WatchKey has been invalidated. Stopping directory monitoring.");
                break; // Sortir de la boucle si la clé est invalide
            }
        }
    }

}
