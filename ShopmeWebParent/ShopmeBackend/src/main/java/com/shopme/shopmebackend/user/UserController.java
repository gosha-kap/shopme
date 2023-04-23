package com.shopme.shopmebackend.user;

import com.shopme.shopmebackend.FileUploadUtil;
import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, model,"firstName","asc",null);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword
                             ) {
        Page<User> page = userService.listByPage(pageNum,sortField,sortDir,keyword);
        var listUsers = page.getContent();
        long startCount = (pageNum - 1) * UserService.USERS_FOR_PAGE + 1;
        long endCount = startCount + UserService.USERS_FOR_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("startCount",startCount);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("endCount",endCount);
        model.addAttribute("users", listUsers);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",reverseSortDir);
        model.addAttribute("keyword",keyword);
        return "users";

    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        List<Role> roles = userService.listRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String dir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(dir);
            FileUploadUtil.saveFile(dir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("messadge", "The user has been saved successfully.");
        return getRedirectURL(user);
    }

    private static String getRedirectURL(User user) {
        String firstPartofEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartofEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {

        try {
            User user = userService.get(id);
            List<Role> roles = userService.listRoles();
            model.addAttribute("roles", roles);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User , id = " + id);
            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messadge", e.getMessage());
            return "redirect:/users";
        }

    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("messadge", "The user , id =" + id + " deleted successfully.");
            return "redirect:/users";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("messadge", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateStatus(@PathVariable(name = "id") Integer id,
                               @PathVariable(name = "status") Boolean status,
                               RedirectAttributes redirectAttributes) {
        userService.updateUserEnableStatus(id, status);
        String statisMess = status ? "enabled" : "disabled";
        String mess = "The user Id = " + id + " has been " + statisMess + ".";
        redirectAttributes.addFlashAttribute("messadge", mess);
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAll();
        UserCsvExporter exporter = new  UserCsvExporter();
        exporter.export(userList,response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAll();
        UserExcelExporter exporter = new  UserExcelExporter();
        exporter.export(userList,response);
    }
    @GetMapping("/users/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAll();
        UserPDFExporter exporter = new  UserPDFExporter();
        exporter.export(userList,response);
    }

}
