package com.helpdesk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdesk.dto.LoginRequest;
import com.helpdesk.entity.*;
import com.helpdesk.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected PriorityRepository priorityRepository;

    @Autowired
    protected StatusRepository statusRepository;

    @Autowired
    protected TicketRepository ticketRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Role userRole;
    protected Role operatorRole;
    protected Role adminRole;

    protected User adminUser;
    protected User operatorUser;
    protected User regularUser;

    protected Category softwareCategory;
    protected Category hardwareCategory;

    protected Priority lowPriority;
    protected Priority mediumPriority;
    protected Priority highPriority;

    protected Status newStatus;
    protected Status inProgressStatus;
    protected Status resolvedStatus;
    protected Status closedStatus;

    @BeforeEach
    public void setUp() {
        // Очистка БД
        commentRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        priorityRepository.deleteAll();
        statusRepository.deleteAll();
        roleRepository.deleteAll();

        // Создание ролей
        userRole = roleRepository.save(new Role(null, "USER"));
        operatorRole = roleRepository.save(new Role(null, "OPERATOR"));
        adminRole = roleRepository.save(new Role(null, "ADMIN"));

        // Создание пользователей
        adminUser = new User();
        adminUser.setLogin("admin");
        adminUser.setPasswordHash(passwordEncoder.encode("password"));
        adminUser.setFullName("Администратор");
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(adminRole);
        adminUser = userRepository.save(adminUser);

        operatorUser = new User();
        operatorUser.setLogin("operator");
        operatorUser.setPasswordHash(passwordEncoder.encode("password"));
        operatorUser.setFullName("Оператор");
        operatorUser.setEmail("operator@test.com");
        operatorUser.setRole(operatorRole);
        operatorUser = userRepository.save(operatorUser);

        regularUser = new User();
        regularUser.setLogin("user");
        regularUser.setPasswordHash(passwordEncoder.encode("password"));
        regularUser.setFullName("Пользователь");
        regularUser.setEmail("user@test.com");
        regularUser.setRole(userRole);
        regularUser = userRepository.save(regularUser);

        // Создание категорий
        softwareCategory = categoryRepository.save(new Category(null, "Программное обеспечение"));
        hardwareCategory = categoryRepository.save(new Category(null, "Оборудование"));

        // Создание приоритетов
        lowPriority = priorityRepository.save(new Priority(null, "Низкий"));
        mediumPriority = priorityRepository.save(new Priority(null, "Средний"));
        highPriority = priorityRepository.save(new Priority(null, "Высокий"));

        // Создание статусов
        newStatus = statusRepository.save(new Status(null, "Новая"));
        inProgressStatus = statusRepository.save(new Status(null, "В работе"));
        resolvedStatus = statusRepository.save(new Status(null, "Решена"));
        closedStatus = statusRepository.save(new Status(null, "Закрыта"));
    }

    /**
     * Получить JWT токен для пользователя
     */
    protected String getTokenFor(String login, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin(login);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    protected String getAdminToken() throws Exception {
        return getTokenFor("admin", "password");
    }

    protected String getOperatorToken() throws Exception {
        return getTokenFor("operator", "password");
    }

    protected String getUserToken() throws Exception {
        return getTokenFor("user", "password");
    }
}
