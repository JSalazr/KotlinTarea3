FROM java:8

RUN git clone https://github.com/JSalazr/KotlinTarea3

CMD cd /KotlinTarea3 && java -jar idk.jar

EXPOSE 8080