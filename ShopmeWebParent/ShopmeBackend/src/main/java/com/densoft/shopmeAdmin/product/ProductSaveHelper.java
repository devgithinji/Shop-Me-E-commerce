package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.util.FileUpload;
import com.densoft.shopmecommon.entity.Product;
import com.densoft.shopmecommon.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
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
            String uploadDir = "../product-images/" + savedProduct.getId();
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, mainImageMultipartFile);
        }

        if (extraImageMultiPartFiles.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultiPartFiles) {
                if (multipartFile.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUpload.saveFile(uploadDir, fileName, multipartFile);
            }
        }

    }


    public static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: " + fileName);
                    } catch (IOException e) {
                        LOGGER.error("could not delete extra image: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }
}
