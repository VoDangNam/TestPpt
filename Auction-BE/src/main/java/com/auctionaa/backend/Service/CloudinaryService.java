package com.auctionaa.backend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${app.cloudinary.base-folder:auctionaa}")
    private String baseFolder; // mặc định "auctionaa"

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Getter @AllArgsConstructor
    public static class UploadResult {
        private String url;      // secure_url
        private String publicId; // public_id
    }

    // Giữ backward-compat cho code cũ (nếu còn gọi)
    public String uploadFile(MultipartFile file) throws IOException {
        ensureValidImage(file);
        Map<?,?> res = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", baseFolder + "/misc",
                        "resource_type", "image",
                        "overwrite", true,
                        "unique_filename", true,
                        "use_filename", true,
                        "transformation", new Transformation().quality("auto").fetchFormat("auto")
                )
        );
        return (String) res.get("secure_url");
    }

    // Core upload có folder + public_id
    public UploadResult uploadImage(MultipartFile file, String folder, String publicId, Transformation tf) throws IOException {
        ensureValidImage(file);
        Map<?,?> res = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", publicId,
                        "overwrite", true,
                        "resource_type", "image",
                        "unique_filename", false,
                        "use_filename", true,
                        "transformation", tf != null ? tf : new Transformation().quality("auto").fetchFormat("auto")
                )
        );
        return new UploadResult((String) res.get("secure_url"), (String) res.get("public_id"));
    }

    // Avatar người dùng: auctionaa/avatars/<userId>/avatar
    public UploadResult uploadUserAvatar(String userId, MultipartFile file) throws IOException {
        String folder = baseFolder + "/avatars/" + userId;
        Transformation tf = new Transformation()
                .width(400).height(400).crop("fill").gravity("face")
                .quality("auto").fetchFormat("auto");
        return uploadImage(file, folder, "avatar", tf);
    }

    // Ảnh tác phẩm: auctionaa/artworks/<artworkId>/main | detail_i
    public UploadResult uploadArtworkMain(String artworkId, MultipartFile file) throws IOException {
        String folder = baseFolder + "/artworks/" + artworkId;
        return uploadImage(file, folder, "main", null);
    }

    public UploadResult uploadArtworkDetail(String artworkId, MultipartFile file, int index) throws IOException {
        String folder = baseFolder + "/artworks/" + artworkId;
        return uploadImage(file, folder, "detail_" + index, null);
    }

    public void deleteByPublicId(String publicId) {
        try { cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()); }
        catch (IOException ignored) {}
    }

    private void ensureValidImage(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File rỗng");
        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!ct.startsWith("image/"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ cho phép upload image/*");
        if (file.getSize() > 10 * 1024 * 1024) // 10MB
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kích thước ảnh vượt 10MB");
    }
}
