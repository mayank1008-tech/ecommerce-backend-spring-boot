package com.example.ecommerce.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImplements implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        String oldfilename = file.getOriginalFilename();

        String randomId = UUID.randomUUID().toString();
        String newfilename = randomId.concat(oldfilename.substring(oldfilename.lastIndexOf('.')));

        String newpath = path + File.separator + newfilename;

        File folder = new  File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(newpath));

        return newfilename;
    }
}
