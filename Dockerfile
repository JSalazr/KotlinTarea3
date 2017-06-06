FROM python:3

RUN git clone https://github.com/JSalazr/PythonTarea2.git
RUN pip install flask
RUN pip install googlemaps
RUN pip install Image

CMD cd /PythonTarea2 && python Tarea2.py

EXPOSE 8080