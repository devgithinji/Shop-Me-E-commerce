package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.AmazonS3Util;
import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.product.Product;
import com.densoft.shopmecommon.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductSaveHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    public static void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {
        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }


    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }


    public static void setNewExtraImageNames(MultipartFile[] extraImageMultiPartFiles, Product product) {
        if (extraImageMultiPartFiles.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiPartFiles) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }


    public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;
        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }


    public static void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultiPartFiles, Product savedProduct) throws IOException {
        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            String uploadDir = "product-images/" + savedProduct.getId();

            List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");

            for (String objectKey : listObjectKeys) {
                if (!objectKey.contains("/extras/")) {
                    AmazonS3Util.deleteFile(objectKey);
                }
            }

            AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipartFile.getInputStream());
        }

        if (extraImageMultiPartFiles.length > 0) {
            String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultiPartFiles) {
                if (multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
            }
        }

    }


    public static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "product-images/" + product.getId() + "/extras";
        List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

        for (String objectKey : listObjectKeys) {
            int lastIndexOfSlash = objectKey.lastIndexOf("/");
            String fileName = objectKey.substring(lastIndexOfSlash + 1);
            if (!product.containsImageName(fileName)) {
                AmazonS3Util.deleteFile(objectKey);
            }
        }
    }
}
