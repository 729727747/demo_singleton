package com.example.demo_singleton;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

public class main {
    public static GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                // 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("sk-764b48b5004744d5934de29f474893f5")
                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
    }
    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }
    public static void main(String[] args) {
//        test1();


//        testRecord();
        
        testFunction(new Person("张三", "男"), person -> person.name() + "是" + person.gender(), System.out::println);

    }

    private static void testFunction(Person person, Function<Person, String> function, Consumer<String> consumer) {
        String result = function.apply(person);
        consumer.accept(result);
        Consumer<String> stringConsumer = consumer.andThen(System.out::println);
        stringConsumer.accept(result);
    }

    private static void testRecord() {
        Person person = new Person("", "");
        person.validate();
        System.out.println(person);
        System.out.println(person.name());
        System.out.println(person.gender());
        person.play();
    }

    private static void test1() {
        try {
            List<Message> messages = new ArrayList<>();
            messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
            for (int i = 0; i < 3;i++) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("请输入：");
                String userInput = scanner.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                messages.add(createMessage(Role.USER, userInput));
                GenerationParam param = createGenerationParam(messages);
                GenerationResult result = callGenerationWithMessages(param);
                System.out.println("模型输出："+result.getOutput().getChoices().get(0).getMessage().getContent());
                messages.add(result.getOutput().getChoices().get(0).getMessage());
            }
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }
}