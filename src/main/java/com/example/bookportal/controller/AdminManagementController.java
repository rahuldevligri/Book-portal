package com.example.bookportal.controller;

import com.example.bookportal.entity.AuthorEntity;
import com.example.bookportal.dto.AuthorDTO;
import com.example.bookportal.entity.PublisherEntity;
import com.example.bookportal.dto.PublisherDTO;
import com.example.bookportal.entity.UserTypeEntity;
import com.example.bookportal.dto.UserTypeDTO;
import com.example.bookportal.service.AuthorService;
import com.example.bookportal.service.PublisherService;
import com.example.bookportal.service.UserTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Set;

/**
 * Controller for admin management operations.
 * <p>
 * Handles CRUD operations for authors, user types, publishers, and distributors.
 * Provides admin panel endpoints for managing entities.
 */
@Controller
@RequestMapping("/admin")
public class AdminManagementController extends BaseController {

    private static final Long PUBLISHER_TYPE_ID = 1L;
    private static final Long DISTRIBUTOR_TYPE_ID = 2L;
    private static final String PATH_AUTHORS = "/admin/authors";
    private static final String PATH_USER_TYPES = "/admin/user-types";
    private static final String PATH_PUBLISHERS = "/admin/publishers";
    private static final String PATH_PUBLISHERS_ADD = "/admin/publishers/add";
    private static final String PATH_PUBLISHERS_EDIT = "/admin/publishers/edit/";
    private static final String PATH_PUBLISHERS_DELETE = "/admin/publishers/delete";
    private static final String PATH_DISTRIBUTORS = "/admin/distributors";
    private static final String PATH_DISTRIBUTORS_ADD = "/admin/distributors/add";
    private static final String PATH_DISTRIBUTORS_EDIT = "/admin/distributors/edit/";
    private static final String PATH_DISTRIBUTORS_DELETE = "/admin/distributors/delete";
    private static final Set<String> AUTHOR_SORT_FIELDS = Set.of("firstName", "lastName", "email", "id");
    private static final Set<String> USER_TYPE_SORT_FIELDS = Set.of("type", "id");
    private static final Set<String> PARTNER_SORT_FIELDS = Set.of("name", "address", "telephone", "fax", "email", "webSite", "id");

    @Autowired
    private AuthorService authorService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Displays the author management page with pagination and sorting.
     *
     * @param page      current page number
     * @param size      page size
     * @param sort      sort field
     * @param direction sort direction
     * @param model     model to populate view attributes
     * @return view name for admin authors page
     */
    @GetMapping("/authors")
    public String listAuthors(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(defaultValue = "firstName") String sort,
                              @RequestParam(defaultValue = "asc") String direction,
                              Model model) {
        String safeSort = sanitizeSort(sort, "firstName", AUTHOR_SORT_FIELDS);
        String safeDirection = sanitizeDirection(direction);
        Pageable pageable = pageable(page, size, safeSort, safeDirection, 5, 100, "firstName");
        Page<AuthorEntity> authorsPage = authorService.getAuthorsPage(pageable);
        model.addAttribute("authors", authorsPage.getContent());
        addPageMeta(model, authorsPage, size);
        model.addAttribute("sort", safeSort);
        model.addAttribute("direction", safeDirection);
        return "admin-authors";
    }

    /**
     * Displays the add author form.
     * @param model model to populate view attributes
     * @return view name for add author form
     */
    @GetMapping("/authors/add")
    public String addAuthorForm(Model model) {
        if (!model.containsAttribute("authorDTO")) {
            model.addAttribute("authorDTO", new AuthorDTO());
        }
        return showCrudForm(model, "authorDTO", model.getAttribute("authorDTO"), "add", "admin-author-form", PATH_AUTHORS);
    }

    /**
     * Handles submission of add author form.
     * @param authorDTO author data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/authors/add")
    public String addAuthor(@Valid @ModelAttribute("authorDTO") AuthorDTO authorDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showCrudForm(model, "authorDTO", authorDTO, "add", "admin-author-form", PATH_AUTHORS);
        }
        AuthorEntity author = authorService.fromDto(authorDTO);
        try {
            authorService.createAuthor(author);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.add.success"));
            return redirectTo(PATH_AUTHORS);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return showCrudForm(model, "authorDTO", authorDTO, "add", "admin-author-form", PATH_AUTHORS);
        }
    }

    /**
     * Displays the edit author form for a specific author.
     * @param id author ID
     * @param model model to populate view attributes
     * @return view name for edit author form
     */
    @GetMapping("/authors/edit/{id}")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        AuthorEntity author = authorService.getAuthorById(id);
        AuthorDTO authorDTO = authorService.toDto(author);
        if (!model.containsAttribute("authorDTO")) {
            model.addAttribute("authorDTO", authorDTO);
        }
        return showCrudForm(model, "authorDTO", model.getAttribute("authorDTO"), "edit", "admin-author-form", PATH_AUTHORS);
    }

    /**
     * Handles submission of edit author form.
     * @param id author ID
     * @param authorDTO author data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/authors/edit/{id}")
    public String editAuthor(@PathVariable Long id,
                             @Valid @ModelAttribute("authorDTO") AuthorDTO authorDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            authorDTO.setId(id);
            return showCrudForm(model, "authorDTO", authorDTO, "edit", "admin-author-form", PATH_AUTHORS);
        }
        AuthorEntity existingAuthor = authorService.getAuthorById(id);
        if (!authorService.hasChanges(existingAuthor, authorDTO)) {
            redirectAttributes.addFlashAttribute("info", msg("admin.action.nochange"));
            return redirectTo(PATH_AUTHORS);
        }

        AuthorEntity author = authorService.fromDto(authorDTO);
        try {
            authorService.updateAuthor(id, author);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.edit.success"));
            return redirectTo(PATH_AUTHORS);
        } catch (Exception e) {
            authorDTO.setId(id);
            model.addAttribute("error", e.getMessage());
            return showCrudForm(model, "authorDTO", authorDTO, "edit", "admin-author-form", PATH_AUTHORS);
        }
    }

    /**
     * Handles deletion of authors.
     * @param authorIds list of author IDs
     * @param confirm confirmation flag
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or confirmation view
     */
    @PostMapping("/authors/delete")
    public String deleteAuthors(@RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                                @RequestParam(defaultValue = "false") boolean confirm,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(authorIds);
        if (safeIds.isEmpty()) {
            return redirectWithMessage(redirectAttributes, "error", "admin.action.delete.noselection", PATH_AUTHORS);
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "delete.author.confirmation",
                    PATH_AUTHORS + "/delete",
                    PATH_AUTHORS,
                    hiddenParamsForIds("authorIds", safeIds, null)
            );
        }
        try {
            authorService.deleteAuthors(safeIds);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(PATH_AUTHORS);
    }

    /**
     * Displays the user type management page.
     * @param page current page number
     * @param size page size
     * @param sort sort field
     * @param direction sort direction
     * @param model model to populate view attributes
     * @return view name for admin user types page
     */
    @GetMapping("/user-types")
    public String listUserTypes(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(defaultValue = "type") String sort,
                                @RequestParam(defaultValue = "asc") String direction,
                                Model model) {
        String safeSort = sanitizeSort(sort, "type", USER_TYPE_SORT_FIELDS);
        String safeDirection = sanitizeDirection(direction);
        Pageable pageable = pageable(page, size, safeSort, safeDirection, 5, 100, "type");
        Page<UserTypeEntity> userTypesPage = userTypeService.findPage(pageable);
        model.addAttribute("userTypes", userTypesPage.getContent());
        addPageMeta(model, userTypesPage, size);
        model.addAttribute("sort", safeSort);
        model.addAttribute("direction", safeDirection);
        return "admin-user-types";
    }

    /**
     * Displays the add user type form.
     * @param model model to populate view attributes
     * @return view name for add user type form
     */
    @GetMapping("/user-types/add")
    public String addUserTypeForm(Model model) {
        if (!model.containsAttribute("userTypeDTO")) {
            model.addAttribute("userTypeDTO", new UserTypeDTO());
        }
        return showCrudForm(model, "userTypeDTO", model.getAttribute("userTypeDTO"), "add", "admin-user-type-form", PATH_USER_TYPES);
    }

    /**
     * Handles submission of add user type form.
     * @param userTypeDTO user type data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/user-types/add")
    public String addUserType(@Valid @ModelAttribute("userTypeDTO") UserTypeDTO userTypeDTO,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showCrudForm(model, "userTypeDTO", userTypeDTO, "add", "admin-user-type-form", PATH_USER_TYPES);
        }
        UserTypeEntity userType = userTypeService.fromDto(userTypeDTO);
        try {
            userTypeService.create(userType);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.add.success"));
            return redirectTo(PATH_USER_TYPES);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return showCrudForm(model, "userTypeDTO", userTypeDTO, "add", "admin-user-type-form", PATH_USER_TYPES);
        }
    }

    /**
     * Displays the edit user type form for a specific user type.
     * @param id user type ID
     * @param model model to populate view attributes
     * @return view name for edit user type form
     */
    @GetMapping("/user-types/edit/{id}")
    public String editUserTypeForm(@PathVariable Long id, Model model) {
        UserTypeEntity userType = userTypeService.findById(id);
        UserTypeDTO userTypeDTO = userTypeService.toDto(userType);
        if (!model.containsAttribute("userTypeDTO")) {
            model.addAttribute("userTypeDTO", userTypeDTO);
        }
        return showCrudForm(model, "userTypeDTO", model.getAttribute("userTypeDTO"), "edit", "admin-user-type-form", PATH_USER_TYPES);
    }

    /**
     * Handles submission of edit user type form.
     * @param id user type ID
     * @param userTypeDTO user type data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/user-types/edit/{id}")
    public String editUserType(@PathVariable Long id,
                               @Valid @ModelAttribute("userTypeDTO") UserTypeDTO userTypeDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            userTypeDTO.setId(id);
            return showCrudForm(model, "userTypeDTO", userTypeDTO, "edit", "admin-user-type-form", PATH_USER_TYPES);
        }
        UserTypeEntity existingType = userTypeService.findById(id);
        if (!userTypeService.hasChanges(existingType, userTypeDTO)) {
            redirectAttributes.addFlashAttribute("info", msg("admin.action.nochange"));
            return redirectTo(PATH_USER_TYPES);
        }

        UserTypeEntity userType = userTypeService.fromDto(userTypeDTO);
        try {
            userTypeService.update(id, userType);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.edit.success"));
            return redirectTo(PATH_USER_TYPES);
        } catch (Exception e) {
            userTypeDTO.setId(id);
            model.addAttribute("error", e.getMessage());
            return showCrudForm(model, "userTypeDTO", userTypeDTO, "edit", "admin-user-type-form", PATH_USER_TYPES);
        }
    }

    /**
     * Handles deletion of user types.
     * @param ids list of user type IDs
     * @param confirm confirmation flag
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or confirmation view
     */
    @PostMapping("/user-types/delete")
    public String deleteUserTypes(@RequestParam(value = "userTypeIds", required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "false") boolean confirm,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(ids);
        if (safeIds.isEmpty()) {
            return redirectWithMessage(redirectAttributes, "error", "admin.action.delete.noselection", PATH_USER_TYPES);
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "delete.user.type.confirmation",
                    PATH_USER_TYPES + "/delete",
                    PATH_USER_TYPES,
                    hiddenParamsForIds("userTypeIds", safeIds, null)
            );
        }
        try {
            userTypeService.delete(safeIds);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(PATH_USER_TYPES);
    }

        /**
         * Displays the publisher management page with pagination and sorting.
         *
         * @param page      current page number
         * @param size      page size
         * @param sort      sort field
         * @param direction sort direction
         * @param model     model to populate view attributes
         * @return view name for admin publishers page
         */
        @GetMapping("/publishers")
        public String listPublishers(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "5") int size,
                     @RequestParam(defaultValue = "name") String sort,
                     @RequestParam(defaultValue = "asc") String direction,
                     Model model) {
        return listByType(
            PUBLISHER_TYPE_ID,
            "publisher.manager",
            "publisher.list",
            "no.publishers.found",
            "publisherIds",
            PATH_PUBLISHERS_DELETE,
            PATH_PUBLISHERS_ADD,
            PATH_PUBLISHERS_EDIT,
            PATH_PUBLISHERS,
            page,
            size,
            sort,
            direction,
            model
        );
        }

        /**
         * Displays the distributor management page with pagination and sorting.
         *
         * @param page      current page number
         * @param size      page size
         * @param sort      sort field
         * @param direction sort direction
         * @param model     model to populate view attributes
         * @return view name for admin distributors page
         */
        @GetMapping("/distributors")
        public String listDistributors(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String direction,
                       Model model) {
        return listByType(
            DISTRIBUTOR_TYPE_ID,
            "distributors.manager",
            "distributor.list",
            "no.distributors.found",
            "distributorIds",
            PATH_DISTRIBUTORS_DELETE,
            PATH_DISTRIBUTORS_ADD,
            PATH_DISTRIBUTORS_EDIT,
            PATH_DISTRIBUTORS,
            page,
            size,
            sort,
            direction,
            model
        );
        }

    /**
     * Displays the add publisher form.
     * @param model model to populate view attributes
     * @return view name for add publisher form
     */
    @GetMapping("/publishers/add")
    public String addPublisherForm(Model model) {
        if (!model.containsAttribute("publisherDTO")) {
            model.addAttribute("publisherDTO", new PublisherDTO());
        }
        return showPartnerForm(model, "add.publisher", PATH_PUBLISHERS_ADD);
    }

    /**
     * Displays the add distributor form.
     * @param model model to populate view attributes
     * @return view name for add distributor form
     */
    @GetMapping("/distributors/add")
    public String addDistributorForm(Model model) {
        if (!model.containsAttribute("publisherDTO")) {
            model.addAttribute("publisherDTO", new PublisherDTO());
        }
        return showPartnerForm(model, "add.distributor", PATH_DISTRIBUTORS_ADD);
    }

    /**
     * Handles submission of add publisher form.
     * @param publisherDTO publisher data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/publishers/add")
    public String addPublisher(@Valid @ModelAttribute("publisherDTO") PublisherDTO publisherDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showPartnerForm(model, "add.publisher", PATH_PUBLISHERS_ADD);
        }
        PublisherEntity publisher = publisherService.fromDto(publisherDTO);
        try {
            publisherService.createPublisher(publisher, PUBLISHER_TYPE_ID);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.add.success"));
            return redirectTo(PATH_PUBLISHERS);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return showPartnerForm(model, "add.publisher", PATH_PUBLISHERS_ADD);
        }
    }

    /**
     * Handles submission of add distributor form.
     * @param distributorDTO distributor data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/distributors/add")
    public String addDistributor(@Valid @ModelAttribute("publisherDTO") PublisherDTO distributorDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showPartnerForm(model, "add.distributor", PATH_DISTRIBUTORS_ADD);
        }
        PublisherEntity distributor = publisherService.fromDto(distributorDTO);
        try {
            publisherService.createPublisher(distributor, DISTRIBUTOR_TYPE_ID);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.add.success"));
            return redirectTo(PATH_DISTRIBUTORS);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return showPartnerForm(model, "add.distributor", PATH_DISTRIBUTORS_ADD);
        }
    }

    /**
     * Displays the edit publisher form for a specific publisher.
     * @param id publisher ID
     * @param model model to populate view attributes
     * @return view name for edit publisher form
     */
    @GetMapping("/publishers/edit/{id}")
    public String editPublisherForm(@PathVariable Long id, Model model) {
        PublisherEntity publisher = publisherService.getPublisherById(id);
        PublisherDTO publisherDTO = publisherService.toDto(publisher);
        if (!model.containsAttribute("publisherDTO")) {
            model.addAttribute("publisherDTO", publisherDTO);
        }
        return showPartnerForm(model, "edit.publisher", PATH_PUBLISHERS_EDIT + id);
    }

    /**
     * Displays the edit distributor form for a specific distributor.
     * @param id distributor ID
     * @param model model to populate view attributes
     * @return view name for edit distributor form
     */
    @GetMapping("/distributors/edit/{id}")
    public String editDistributorForm(@PathVariable Long id, Model model) {
        PublisherEntity distributor = publisherService.getPublisherById(id);
        PublisherDTO distributorDTO = publisherService.toDto(distributor);
        if (!model.containsAttribute("publisherDTO")) {
            model.addAttribute("publisherDTO", distributorDTO);
        }
        return showPartnerForm(model, "edit.distributor", PATH_DISTRIBUTORS_EDIT + id);
    }

    /**
     * Handles submission of edit publisher form.
     * @param id publisher ID
     * @param publisherDTO publisher data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/publishers/edit/{id}")
    public String editPublisher(@PathVariable Long id,
                                @Valid @ModelAttribute("publisherDTO") PublisherDTO publisherDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            publisherDTO.setId(id);
            return showPartnerForm(model, "edit.publisher", PATH_PUBLISHERS_EDIT + id);
        }
        PublisherEntity existingPublisher = publisherService.getPublisherById(id);
        if (!publisherService.hasChanges(existingPublisher, publisherDTO)) {
            redirectAttributes.addFlashAttribute("info", msg("admin.action.nochange"));
            return redirectTo(PATH_PUBLISHERS);
        }

        PublisherEntity publisher = publisherService.fromDto(publisherDTO);
        try {
            publisherService.updatePublisher(id, publisher);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.edit.success"));
            return redirectTo(PATH_PUBLISHERS);
        } catch (Exception e) {
            publisherDTO.setId(id);
            model.addAttribute("error", e.getMessage());
            return showPartnerForm(model, "edit.publisher", PATH_PUBLISHERS_EDIT + id);
        }
    }

    /**
     * Handles submission of edit distributor form.
     * @param id distributor ID
     * @param distributorDTO distributor data
     * @param bindingResult validation result
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or form view
     */
    @PostMapping("/distributors/edit/{id}")
    public String editDistributor(@PathVariable Long id,
                                  @Valid @ModelAttribute("publisherDTO") PublisherDTO distributorDTO,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            distributorDTO.setId(id);
            return showPartnerForm(model, "edit.distributor", PATH_DISTRIBUTORS_EDIT + id);
        }
        PublisherEntity existingDistributor = publisherService.getPublisherById(id);
        if (!publisherService.hasChanges(existingDistributor, distributorDTO)) {
            redirectAttributes.addFlashAttribute("info", msg("admin.action.nochange"));
            return redirectTo(PATH_DISTRIBUTORS);
        }

        PublisherEntity distributor = publisherService.fromDto(distributorDTO);
        try {
            publisherService.updatePublisher(id, distributor);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.edit.success"));
            return redirectTo(PATH_DISTRIBUTORS);
        } catch (Exception e) {
            distributorDTO.setId(id);
            model.addAttribute("error", e.getMessage());
            return showPartnerForm(model, "edit.distributor", PATH_DISTRIBUTORS_EDIT + id);
        }
    }

    /**
     * Handles deletion of publishers.
     * @param ids list of publisher IDs
     * @param confirm confirmation flag
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or confirmation view
     */
    @PostMapping("/publishers/delete")
    public String deletePublishers(@RequestParam(value = "publisherIds", required = false) List<Long> ids,
                                   @RequestParam(defaultValue = "false") boolean confirm,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(ids);
        if (safeIds.isEmpty()) {
            return redirectWithMessage(redirectAttributes, "error", "admin.action.delete.noselection", PATH_PUBLISHERS);
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "delete.publisher.confirmation",
                    PATH_PUBLISHERS + "/delete",
                    PATH_PUBLISHERS,
                    hiddenParamsForIds("publisherIds", safeIds, null)
            );
        }
        try {
            publisherService.deletePublishers(safeIds);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(PATH_PUBLISHERS);
    }

    /**
     * Handles deletion of distributors.
     * @param ids list of distributor IDs
     * @param confirm confirmation flag
     * @param model model to populate view attributes
     * @param redirectAttributes redirect attributes for feedback
     * @return redirect or confirmation view
     */
    @PostMapping("/distributors/delete")
    public String deleteDistributors(@RequestParam(value = "distributorIds", required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "false") boolean confirm,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        List<Long> safeIds = uniquePositiveIds(ids);
        if (safeIds.isEmpty()) {
            return redirectWithMessage(redirectAttributes, "error", "admin.action.delete.noselection", PATH_DISTRIBUTORS);
        }
        if (!confirm) {
            return showDeleteConfirmation(
                    model,
                    "delete.distributor.confirmation",
                    PATH_DISTRIBUTORS + "/delete",
                    PATH_DISTRIBUTORS,
                    hiddenParamsForIds("distributorIds", safeIds, null)
            );
        }
        try {
            publisherService.deletePublishers(safeIds);
            redirectAttributes.addFlashAttribute("success", msg("admin.action.delete.success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectTo(PATH_DISTRIBUTORS);
    }

    /**
     * Displays a list of partners (publishers/distributors) by type with pagination and sorting.
     * @param entityTypeId partner type ID
     * @param managerTitleKey manager title key
     * @param listTitleKey list title key
     * @param emptyMessageKey empty message key
     * @param checkboxName checkbox name
     * @param deleteActionPath delete action path
     * @param addPath add path
     * @param editBasePath edit base path
     * @param pageBasePath page base path
     * @param page current page number
     * @param size page size
     * @param sort sort field
     * @param direction sort direction
     * @param model model to populate view attributes
     * @return view name for partner list
     */
    private String listByType(Long entityTypeId,
                              String managerTitleKey,
                              String listTitleKey,
                              String emptyMessageKey,
                              String checkboxName,
                              String deleteActionPath,
                              String addPath,
                              String editBasePath,
                              String pageBasePath,
                              int page,
                              int size,
                              String sort,
                              String direction,
                              Model model) {
        String safeSort = sanitizeSort(sort, "name", PARTNER_SORT_FIELDS);
        String safeDirection = sanitizeDirection(direction);
        Pageable pageable = pageable(page, size, safeSort, safeDirection, 5, 100, "name");
        Page<PublisherEntity> results = publisherService.getPublishersByType(entityTypeId, pageable);
        model.addAttribute("partners", results.getContent());
        model.addAttribute("managerTitleKey", managerTitleKey);
        model.addAttribute("listTitleKey", listTitleKey);
        model.addAttribute("emptyMessageKey", emptyMessageKey);
        model.addAttribute("checkboxName", checkboxName);
        model.addAttribute("deleteActionPath", deleteActionPath);
        model.addAttribute("addPath", addPath);
        model.addAttribute("editBasePath", editBasePath);
        model.addAttribute("pageBasePath", pageBasePath);
        String commonParams = "&size=" + safePageSize(size) + "&sort=" + safeSort + "&direction=" + safeDirection;
        model.addAttribute("prevPagePath", pageBasePath + "?page=" + Math.max(results.getNumber() - 1, 0) + commonParams);
        model.addAttribute("nextPagePath", pageBasePath + "?page=" + (results.getNumber() + 1) + commonParams);
        addPageMeta(model, results, size);
        model.addAttribute("sort", safeSort);
        model.addAttribute("direction", safeDirection);
        return "admin-partner-list";
    }

    private String sanitizeSort(String sort, String defaultSort, Set<String> allowed) {
        if (sort == null || sort.isBlank()) {
            return defaultSort;
        }
        String requested = sort.trim();
        return allowed.contains(requested) ? requested : defaultSort;
    }

    private String sanitizeDirection(String direction) {
        return "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
    }

    /**
     * Prepares the partner form model attributes.
     * @param model model to populate view attributes
     * @param partnerTitleKey partner title key
     * @param partnerActionPath partner action path
     */
    private void preparePartnerForm(Model model, String partnerTitleKey, String partnerActionPath) {
        model.addAttribute("partnerTitleKey", partnerTitleKey);
        model.addAttribute("partnerActionPath", partnerActionPath);
    }

    /**
     * Shows a CRUD form for entity management.
     * @param model model to populate view attributes
     * @param attributeName attribute name
     * @param value attribute value
     * @param mode form mode (add/edit)
     * @param view view name
     * @param cancelPath cancel path
     * @return view name for CRUD form
     */
    private String showCrudForm(Model model, String attributeName, Object value, String mode, String view, String cancelPath) {
        model.addAttribute(attributeName, value);
        model.addAttribute("mode", mode);
        model.addAttribute("cancelPath", cancelPath);
        return view;
    }

    /**
     * Shows a partner form for publisher/distributor management.
     * @param model model to populate view attributes
     * @param partnerTitleKey partner title key
     * @param partnerActionPath partner action path
     * @param partner partner entity
     * @return view name for partner form
     */
    private String showPartnerForm(Model model, String partnerTitleKey, String partnerActionPath) {
        preparePartnerForm(model, partnerTitleKey, partnerActionPath);
        model.addAttribute("cancelPath", partnerActionPath.contains("/distributors/") || partnerActionPath.endsWith("/distributors/add") ? PATH_DISTRIBUTORS : PATH_PUBLISHERS);
        return "admin-partner-form";
    }

    private String redirectWithMessage(RedirectAttributes redirectAttributes, String attributeName, String messageKey, String path) {
        redirectAttributes.addFlashAttribute(attributeName, msg(messageKey));
        return redirectTo(path);
    }

    /**
     * Retrieves a localized message by key.
     * @param key message key
     * @return localized message
     */
    private String msg(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * Returns a redirect path string.
     * @param path path to redirect
     * @return redirect string
     */
    private String redirectTo(String path) {
        return "redirect:" + path;
    }

}
