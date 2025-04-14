package com.kaydev.appstore.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kaydev.appstore.models.dto.utils.AppFileInfo;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.bean.IconFace;

@Service
public class AppParser {
    @Autowired
    private AwsService awsService;

    public AwsService getAwsService() {
        return awsService;
    }

    public AppFileInfo parseApkFile(MultipartFile file) throws IOException, CertificateException {

        String ext = getFileExtension(file);
        if (ext.equalsIgnoreCase("apk")) {

            File tempFile = File.createTempFile("app_file_" + System.currentTimeMillis(), "." + ext);
            file.transferTo(tempFile);
            try (ApkFile apkFile = new ApkFile(tempFile)) {
                if (apkFile.getApkSingers().isEmpty() && apkFile.getApkV2Singers().isEmpty()) {
                    throw new RuntimeException("App is not signed. Only signed apps are supported.");
                }

                ApkMeta apkMeta = apkFile.getApkMeta();

                String appName = apkMeta.getName();
                String packageName = apkMeta.getPackageName();
                String versionName = apkMeta.getVersionName();
                Long versionCode = apkMeta.getVersionCode();

                List<IconFace> iconFaces = apkFile.getAllIcons();
                byte[] iconBytes = null;
                if (iconFaces != null && !iconFaces.isEmpty()) {
                    IconFace iconFace = iconFaces.get(0);
                    iconBytes = apkFile.getFileData(iconFace.getPath());
                }

                String iconFileUrl = null;
                if (iconBytes != null) {

                    File tempIconFile = File.createTempFile("app_icon_" +
                            System.currentTimeMillis(), ".png");
                    try (FileOutputStream fos = new FileOutputStream(tempIconFile)) {
                        fos.write(iconBytes);
                    }

                    iconFileUrl = awsService.uploadFile("app-icons/" + tempIconFile.getName(), tempIconFile);
                    tempIconFile.delete();
                }

                String targetSdk = apkMeta.getTargetSdkVersion();
                String compileSdk = apkMeta.getCompileSdkVersion();
                String minSdk = apkMeta.getMinSdkVersion();
                String maxSdk = apkMeta.getCompileSdkVersion();
                String size = (tempFile.length() / 1024 / 1024) + "MB";
                List<String> permissions = apkMeta.getUsesPermissions();

                String fileUrl = awsService.uploadFile("apps/" + tempFile.getName(), tempFile);
                // String fileUrl = null;
                tempFile.delete();

                return AppFileInfo.builder().appName(appName).packageName(packageName).versionName(versionName)
                        .versionCode(versionCode).icon(iconFileUrl).size(size).file(fileUrl)
                        .permissions(permissions).targetSdk(targetSdk)
                        .compileSdk(compileSdk).maxSdk(maxSdk).minSdk(minSdk).build();
            } catch (Exception e) {
                throw new RuntimeException("App is debug. Only production apps are supported.");
            }

        } else {
            throw new RuntimeException("Not an apk file");
        }

    }

    public AppFileInfo parseLinuxFile(MultipartFile file) throws IOException {

        String ext = getFileExtension(file);
        if (ext.equalsIgnoreCase("bin")) {
            File tempFile = File.createTempFile("app_file_" + System.currentTimeMillis(), "." + ext);
            file.transferTo(tempFile);

            String fileUrl = awsService.uploadFile("apps/" + tempFile.getName(), tempFile);

            String size = (file.getSize() / 1024 / 1024) + "MB";

            String appName = null;
            String packageName = null;
            String versionName = null;
            Long versionCode = null;
            List<String> permissions = new ArrayList<String>();
            String targetSdk = null;
            String compileSdk = null;
            String minSdk = null;
            String maxSdk = null;
            String iconString = "https://itexstore.s3.us-west-2.amazonaws.com/app-icons/linux_app_logo.png";

            tempFile.delete();

            return AppFileInfo.builder().appName(appName).packageName(packageName).versionName(versionName)
                    .versionCode(versionCode).icon(iconString).size(size).file(fileUrl)
                    .permissions(permissions).targetSdk(targetSdk)
                    .compileSdk(compileSdk).maxSdk(maxSdk).minSdk(minSdk).build();
        } else {
            throw new RuntimeException("Not a linux file");
        }

    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return originalFilename.substring(lastDotIndex + 1).toLowerCase();
            }
        }
        throw new IllegalArgumentException("Invalid file");
    }
}
