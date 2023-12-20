package com.ssafy.imgMaker22.model.service;

import com.ssafy.imgMaker22.model.dto.GeneratedImage;
import com.ssafy.imgMaker22.model.repository.MainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService{

    private MainRepository mainRepository;

    private S3Uploader s3Uploader;

    // MultipartFile을 사용 안하도록 변경함 (어자피 이미지를 MultipartFile로 받을게 아니라 byte[]로 받을 것이기 때문)
    public String fileUpload(byte[] decodedBytes, GeneratedImage gImage) throws IOException{
        String storedFileUrl = null;

        File file = convert(decodedBytes).get();

        // 같은 파일명이 중복되는지 확인하는 로직이 필요함
        storedFileUrl = s3Uploader.upload(file, "images"); // 경로명을 어떻게 해야 하지??
        gImage.setUrl(storedFileUrl);
        mainRepository.save(gImage);

        return storedFileUrl;
    }

    private Optional<File> convert(byte[] decodedBytes) throws IOException {
        File file = new File("filename"); // 이름 (경로) 가져오고 (수정필요)
        if(file.createNewFile()) { // 같은 이름의 파일이 없다면
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes); // 데이터 넣어주고
            }
            return Optional.of(file); // 리턴
        }
        return Optional.empty();
    }

}