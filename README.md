🌐 CodeNews – Plataforma Inteligente de Newsletter Tech com Spring Boot 3, Apache Kafka, JSoup e Spring Mail

Uma solução completa para coleta, processamento e distribuição de notícias de tecnologia, que combina:

Spring Boot 3: backend modular e extensível, com configuração externalizada (application.yml) e suporte a perfis dev/prod.

Apache Kafka: pipeline desacoplada de mensagens (news-topic), garantindo alta escalabilidade e resiliência na produção e no consumo de dados.

JSoup: web scraping automatizado para extrair títulos, resumos e links de diversas fontes de tecnologia.

Spring Data JPA (H2 em ambiente de desenvolvimento e PostgreSQL em produção): persistência de entidades News e Subscriber, com repositórios customizados para buscas por data, palavra-chave e status.

Spring Mail: envio assíncrono e template-driven de newsletters programadas, gerenciadas pelo NewsletterScheduler.

PipelineService: orquestra todo o fluxo — desde a captura das notícias até o envio final de e-mail — com lógica separada em produtor/consumidor Kafka (NewsProducerService e NewsConsumerService).

Estrutura RESTful: endpoints para CRUD de notícias e assinantes, filtragem, paginação e exportação de dados, via NewsController e SubscriberController.

Boas práticas de engenharia: uso de Lombok para reduzir boilerplate, Docker para containerização, testes unitários/integrados, Conventional Commits e convenções de branch.

Uma arquitetura robusta que automatiza o pipeline de coleta (scraping) → persistência (JPA) → mensageria (Kafka) → montagem de conteúdo (PipelineService) → envio de email (Spring Mail), garantindo alta disponibilidade e fácil manutenção.
