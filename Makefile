JAVAC = javac
JAVA = java

SRC_RAW = Main.java MainWindow.java Clock.java AlarmClock.java ClockHand.java AlarmClockHand.java
SRC_DIR = src
SRC = $(addprefix $(SRC_DIR)/,$(SRC_RAW))

IMAGE_RAW = face.gif hour.png minute.png second.png alarm_hour.png alarm_minute.png alarm_hour_red.png alarm_minute_red.png
IMAGE_DIR = image
IMAGE = $(addprefix $(IMAGE_DIR)/,$(IMAGE_RAW))

AUDIO_RAW = normal.wav fast.wav
AUDIO_DIR = sound
AUDIO = $(addprefix $(AUDIO_DIR)/,$(AUDIO_RAW))

OBJ_RAW = $(SRC_RAW:.java=.class)
OBJDIR = objects
OBJ = $(addprefix $(OBJDIR)/,$(OBJ_RAW))

ROOT_DIR = $(realpath .)
ifeq ($(OS),Windows_NT)
	CLASSPATH = "$(addprefix $(ROOT_DIR)/,$(OBJDIR));$(addprefix $(ROOT_DIR)/,$(IMAGE_DIR));$(addprefix $(ROOT_DIR)/,$(AUDIO_DIR))"
else
	UNAME = $(shell uname)
	ifneq (,$(findstring $(UNAME),Linux Darwin))
		CLASSPATH = "$(addprefix $(ROOT_DIR)/,$(OBJDIR)):$(addprefix $(ROOT_DIR)/,$(IMAGE_DIR)):$(addprefix $(ROOT_DIR)/,$(AUDIO_DIR))"
	endif
endif


all: clock

obj_dir:
	@if [ ! -d "$(OBJDIR)" ]; then \
	    echo "Creating Object Directory..."; \
	    mkdir "$(OBJDIR)"; \
	fi

clock: obj_dir $(SRC) $(IMAGE) $(AUDIO)
	$(JAVAC) -cp $(CLASSPATH) $(SRC) -d $(OBJDIR)

run: $(OBJ) $(IMAGE) $(AUDIO)
	$(JAVA) -cp $(CLASSPATH) Main ${ARGS}

.PHONY: clean
clean:
	@if [ -d "$(OBJDIR)" ]; then \
		echo "Cleaning Object Directory..."; \
		rm -R "$(OBJDIR)"; \
	fi
