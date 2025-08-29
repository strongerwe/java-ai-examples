# java-ai-examples

> Java攻城狮AI大模型项目对接学习示例参考

## 1. 基础框架版本

- JDK17
- SpringBoot 3.4.5
- SpringAI 1.0.1
- SpringAI Alibaba(SAA) 1.0.0.3

## 2. chat-example

### 2.1 curl-example
#### a. 调用OpenAI接口
> OpenAI注意返回429错误
> 
> You exceeded your current quota, please check your plan and billing details. For more information on this error, read the docs: https://platform.openai.com/docs/guides/error-codes/api-errors.
```shell
curl --location 'https://api.openai.com/v1/responses' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer ${OPENAI_API_KEY}' \
--data '{
    "model": "gpt-5",
    "input": "你好你是什么大模型？"
  }'
```
#### b. 调用阿里云dashscope接口
- 兼容OpenAI
```shell
curl --location 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer ${DASHSCOPE_API_KEY}' \
--data '{
    "model": "qwen-plus",
    "stream": false,
    "messages": [
        {
            "role": "user", 
            "content": "你好，你是什么大模型？"
        }
    ]
}'
```
- 阿里云百炼API
```shell
curl --location 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer ${DASHSCOPE_API_KEY}' \
--data '{
    "model": "deepseek-r1",
    "input": {
        "messages": [
            {
                "role": "user",
                "content": "你是谁？"
            }
        ]
    },
    "parameters": {
        "result_format": "message"
    }
}'
```
#### c. 调用DeepSeekAPI
> 兼容openAI标准API接口
```shell
curl --location 'https://api.deepseek.com/v1/chat/completions' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer ${DEEPSEEK_API_KEY}' \
--data '{
        "model": "deepseek-chat",
        "messages": [
          {"role": "user", "content": "你好!你是谁？"}
        ],
        "stream": false
      }'
```

#### d. 调用本地Ollama
> 兼容openAI标准API接口
```shell
curl --location 'http://127.0.0.1:11434/v1/chat/completions' \
--header 'Content-Type: application/json' \
--data '{
    "model": "deepseek-r1:1.5b",
    "stream": false,
    "messages": [
        {
            "role": "user", 
            "content": "你是谁？"
        }
    ]
}'
```
### 2.2 code-example

#### a. langchain4j-ai-chat

> 基于langchain4j框架实现chat示例

#### b. spring-ai-alibaba-chat

> 基于spring-ai-alibaba框架实现chat示例

#### c. spring-ai-chat

> 基于spring-ai框架实现chat示例

#### d. spring-ai-deepseek-chat

> 基于spring-ai对接deepseek实现chat示例

#### e. spring-ai-ollama-chat

> 基于spring-ai对接ollama实现chat示例
