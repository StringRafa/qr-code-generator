package com.panamby.qr_code_generator.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.WriterException;
import org.springframework.http.MediaType;
import com.panamby.qr_code_generator.services.QRCodeService;

import net.sourceforge.tess4j.TesseractException;

@Controller
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/generateQRCode")
    public ResponseEntity<byte[]> generateQRCode(@RequestParam String link,
                                                 @RequestParam int width,
                                                 @RequestParam int height) {
        try {
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(link, width, height);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/generateQRCodeFromPDF")
    public ResponseEntity<byte[]> generateQRCodeFromPDF(@RequestParam("file") MultipartFile file,
                                                        @RequestParam int width,
                                                        @RequestParam int height) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            String pdfText = qrCodeService.extractTextFromPDF(convFile);
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(pdfText, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (IOException | WriterException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/generateQRCodeFromImage")
    public ResponseEntity<byte[]> generateQRCodeFromImage(@RequestParam("file") MultipartFile file,
                                                          @RequestParam int width,
                                                          @RequestParam int height) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            String extractedText = qrCodeService.extractTextFromImage(convFile);
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(extractedText, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (IOException | TesseractException | WriterException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/decodeQRCode")
    public ResponseEntity<byte[]> decodeQRCode(@RequestParam("file") MultipartFile file,
                                               @RequestParam int width,
                                               @RequestParam int height) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            String decodedText = qrCodeService.decodeQRCode(convFile);
            byte[] image = qrCodeService.generateImageFromText(decodedText, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
