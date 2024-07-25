package it.zetlark.awsintegration.common.controller;

import it.zetlark.awsintegration.common.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;

@RestController("aws-s3-controller")
@RequestMapping("/s3-integration")
public class AWSS3Controller {

    @Autowired
    private AWSS3Service awss3Service;

    @PostMapping("/upload")
    public void saveDocument(
            @RequestPart("file") MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        System.out.println(awss3Service.upload(file));
    }

    @GetMapping("/download")
    public void download(
            @PathParam("url") String url,
            HttpServletRequest request,
            HttpServletResponse response) {
        Resource resource = awss3Service.downloadAwsFile(url);
        String[] urlSplitted = url.split("/");
        var fileName = urlSplitted[urlSplitted.length -1];
        extractDocumentToResponse(request, response, resource, fileName);

    }


    public void extractDocumentToResponse(final HttpServletRequest request, final HttpServletResponse response, final Resource resource, final String fileName) {
        final String contentType = getContentType(resource, request);
        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue;
        try {
            headerValue = "attachment; filename=" + fileName;
            response.setHeader(headerKey, headerValue);

            resource.getInputStream().transferTo(response.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error encounter during download file");
        }
    }

    public String getContentType(Resource resource, HttpServletRequest request) {
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

}
