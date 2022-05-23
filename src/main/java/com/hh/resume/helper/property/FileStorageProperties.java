package com.hh.resume.helper.property;

import com.hh.resume.dto.EmployeeDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
@RequiredArgsConstructor
public class FileStorageProperties {
    private static Map<String,Path> paths = new HashMap<>();
    private String uploadDir = "C:\\Users\\callicoder\\uploads";

    public static void deleteFile(String pathFile, EmployeeDTO employeeDTO){
        Path path = paths.get(pathFile);
        if (path == null) return;
        employeeDTO.setPhotoUri(null);
        employeeDTO.setPhotoUrl(null);
        try {
            Files.delete(path);
            paths.remove(pathFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addFilePath(String pathFile, Path path){
        paths.put(pathFile, path);
    }
}
