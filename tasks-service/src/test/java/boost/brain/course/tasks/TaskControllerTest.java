package boost.brain.course.tasks;

import boost.brain.course.tasks.controller.TasksController;
import boost.brain.course.tasks.controller.dto.TaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static boost.brain.course.tasks.Constants.CREATE_PREFIX;
import static boost.brain.course.tasks.Constants.TASKS_CONTROLLER_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedPostgresTest
@Log
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TasksController tasksController;

    private ResultActions mockMvcResult;

    private TaskDto taskDto;
    private TaskDto taskDtoResult;


    @Test
    public void testControllerIsNotNULL() {
        log.info("Тестирование контроллера на NotNull");
        assertThat(tasksController).isNotNull();
    }

    @Test
    public void testApiWithCreateGoodObject() {
        log.info("тестирование API для образцового объекта без дефектов");
        taskDto = initSampleObjectTaskDto();

        ResultActions resultActions;
        try {
            resultActions = saveInstanceTaskToDB(taskDto);
        } catch (Exception e) {
            log.severe("ошибка при отправке запроса");
            fail();
            return;
        }
        TaskDto taskDtoFromAPI = receiveTaskDtoFromResultAction(resultActions);
        if (taskDtoFromAPI == null) {
            fail();
        } else {
            assertEquals(taskDto.getAuthor(), taskDtoFromAPI.getAuthor());
        }
    }


    @Test
    public void testApiWithBadProjectId() {
        log.info("тестирование API для образцового объекта с дефектным id проекта");
        taskDto = initSampleObjectTaskDto();
        taskDto.setProject(0);
        ResultActions resultActions;
        try {
            resultActions = saveInstanceTaskToDB(taskDto);
        } catch (Exception e) {
            log.severe("ошибка при отправке запроса");
            fail();
            return;
        }
        try {
            assertThat(resultActions.andReturn().getResolvedException().toString().endsWith("NotFoundException"));
        } catch (NullPointerException e) {
            fail();
        }
    }

    /*
    @Test
    public void createWithBadAuthor() throws Exception {
        initSampleObjectTaskDto();
        String author = "BadAuthor";
        taskDto.setAuthor(author);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void createWithEmptyAuthor() throws Exception {
        initSampleObjectTaskDto();
        String author = "";
        taskDto.setAuthor(author);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void createWithBadImplementer() throws Exception {
        initSampleObjectTaskDto();
        String implementer = "BadImplementer";
        taskDto.setImplementer(implementer);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void createWithEmptyImplementer() throws Exception {
        initSampleObjectTaskDto();
        String implementer = "";
        taskDto.setImplementer(implementer);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void createWithEmptyName() throws Exception {
        initSampleObjectTaskDto();
        String name = "";
        taskDto.setName(name);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void createWithEmptyText() throws Exception {
        initSampleObjectTaskDto();
        String text = "";
        taskDto.setText(text);
        saveInstanceTaskToDB();
        assertThat(mockMvcResult.andReturn().getResolvedException()).isNotNull();
    }

    @Test
    public void count() throws Exception {
        initSampleObjectTaskDto();
        saveInstanceTaskToDB();
        saveInstanceTaskToDB();
        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + COUNT_PREFIX));
        Long count = Long.parseLong(mockMvcResult.andReturn().getResponse().getContentAsString());
        assertNotNull(count);
        assertTrue(count > 0);
    }

    @Test
    public void readTest() throws Exception {
        initSampleObjectTaskDto();
        saveInstanceTaskToDB();
        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + READ_PREFIX + "/" + taskDtoResult.getId()));
        mockMvcResult.andDo(print())
                .andExpect(status().isOk());
        TaskDto taskDtoResultRead = mapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), TaskDto.class);
        mockMvcResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is((Integer.parseInt(String.valueOf(taskDtoResultRead.getId()))))));
    }

    @Test
    public void updateTest() throws Exception {
        initSampleObjectTaskDto();
        saveInstanceTaskToDB();

        taskDtoResult.setName("NEW PROJECT");
        mockMvcResult = mockMvc.perform(patch(TASKS_CONTROLLER_PREFIX + UPDATE_PREFIX)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJsonString(taskDtoResult)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + READ_PREFIX + "/" + taskDtoResult.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.project", is(taskDtoResult.getProject())));

    }

    @Test
    public void deleteTest() throws Exception {
        initSampleObjectTaskDto();
        saveInstanceTaskToDB();
        mockMvc.perform(delete(TASKS_CONTROLLER_PREFIX + DELETE_PREFIX + "/" + taskDtoResult.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + READ_PREFIX + "/" + taskDtoResult.getId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete(TASKS_CONTROLLER_PREFIX + DELETE_PREFIX + "/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void pageTest() throws Exception {
        initSampleObjectTaskDto();
        for (int i = 0; i < 20; i++) {
            saveInstanceTaskToDB();
        }

        int page = 2;
        int size = 5;
        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + PAGE_PREFIX + "/" + page + "/" + size))
                .andDo(print()).andExpect(status().isOk());
        String listTaskAsString = mockMvcResult.andReturn().getResponse().getContentAsString();
        List<TaskDto> tasks = Arrays.asList(mapper.readValue(listTaskAsString, TaskDto[].class));
        for (TaskDto task : tasks) {
            System.out.println(task);
        }

        page = 0;
        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + PAGE_PREFIX + "/" + page + "/" + size))
                .andDo(print()).andExpect(status().isNotFound());

        page = 1;
        size = 0;
        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + PAGE_PREFIX + "/" + page + "/" + size))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void taskForTest() throws Exception {
        initSampleObjectTaskDto();
        String implementer = "cher@mail.ru";
        taskDto.setImplementer(implementer);
        for (int i = 0; i < 3; i++) {
            saveInstanceTaskToDB();
        }

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FOR_PREFIX + "/" + implementer))
                .andDo(print()).andExpect(status().isOk());
        List<TaskDto> tasks = Arrays.asList(mapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), TaskDto[].class));
        assertThat(tasks.size()).isGreaterThanOrEqualTo(3);

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FOR_PREFIX + "/" + "qwe"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void tasksFromTest() throws Exception {
        initSampleObjectTaskDto();
        String author = "boostbrain@gmail.com";
        taskDto.setAuthor(author);
        for (int i = 0; i < 3; i++) {
            saveInstanceTaskToDB();
        }

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FROM_PREFIX + "/" + author))
                .andDo(print()).andExpect(status().isOk());
        List<TaskDto> tasks = Arrays.asList(mapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), TaskDto[].class));
        assertThat(tasks.size()).isGreaterThanOrEqualTo(3);

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FROM_PREFIX + "/" + "qwe"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void taskInTest() throws Exception {
        initSampleObjectTaskDto();
        int project = 777;
        taskDto.setProject(project);
        for (int i = 0; i < 7; i++) {
            saveInstanceTaskToDB();
        }

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + IN_PREFIX + "/" + project))
                .andDo(print()).andExpect(status().isOk());
        List<TaskDto> tasks = Arrays.asList(mapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), TaskDto[].class));
        assertThat(tasks.size()).isGreaterThanOrEqualTo(7);

        mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + IN_PREFIX + "/987654321"))
                .andDo(print()).andExpect(jsonPath("$").isEmpty());

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + IN_PREFIX + "/-222"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void filterTest() throws Exception {
        initSampleObjectTaskDto();
        int project = 111222;
        String author = "qwe@qwe.com";
        String implementer = "asd@asd.com";
        taskDto.setProject(project);
        taskDto.setAuthor(author);
        taskDto.setImplementer(implementer);
        for (int i = 0; i < 3; i++) {
            saveInstanceTaskToDB();
        }

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FILTER_PREFIX)
                .param("project", Integer.toString(project))
                .param("author", author)
                .param("implementer", implementer)
        );
        List<TaskDto> tasks = Arrays.asList(mapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), TaskDto[].class));
        assertThat(tasks.size()).isGreaterThanOrEqualTo(3);

        mockMvcResult = mockMvc.perform(get(TASKS_CONTROLLER_PREFIX + FILTER_PREFIX)
                .param("project", "999555111"))
                .andDo(print())
                .andExpect(jsonPath("$").isEmpty());

    }

    */


    /**
     * Инициализация тестового образцового объекта -TaskDTO
     */
    private TaskDto initSampleObjectTaskDto() {
        taskDto = new TaskDto();
        taskDto.setProject(1);
        taskDto.setAuthor("boost.brain@gmail.com");
        taskDto.setImplementer("chernov_serg@mail.ru");
        taskDto.setName("java-incubator");
        taskDto.setText("Утилита тестирования сервиса управления заданиями");
        return taskDto;
    }

    private String asJsonString(final Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Записывает объект taskDTO в БД, с помощью post запроса API-сервиса
     *
     * @param taskDto объект для записи
     * @return результат запроса
     * @throws Exception при ошибке сервиса
     */

    private ResultActions saveInstanceTaskToDB(@NonNull TaskDto taskDto) throws Exception {
        return mockMvc.perform(post(TASKS_CONTROLLER_PREFIX + CREATE_PREFIX)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(asJsonString(taskDto)).header("sessionId", "vvvbbbvvv")
        );
    }

    /***
     * Получает объект из результата запроса
     * @param mockMvcResult результат звапроса
     * @return объект класса TaskDto,
     * возращвет null если объект, не удается получить
     */
    private @Nullable
    TaskDto receiveTaskDtoFromResultAction(ResultActions mockMvcResult) {
        String contentJsonResult;
        try {
            contentJsonResult = mockMvcResult.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            log.severe("Неудается получчить JSON данные по объекту" + mockMvcResult.toString());
            return null;
        }
        if (contentJsonResult.isEmpty()) {
            log.severe("содержание JSON ответа сервера - null");
            return null;
        }
        try {
            return mapper.readValue(contentJsonResult, TaskDto.class);
        } catch (IOException e) {
            log.severe("Невозможно получить объект из JSON" + contentJsonResult);
            return null;
        }
    }


}
