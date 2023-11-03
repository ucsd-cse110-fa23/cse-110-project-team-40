# Makefile

# Dependencies
JAVAC = javac
JAVA = java
ABSOLUTE_PROJECT_DIR = $(shell pwd)
JUNIT = $(ABSOLUTE_PROJECT_DIR)/lib/junit-4.13.2.jar
OPENJFX = $(ABSOLUTE_PROJECT_DIR)/lib/javafx-sdk-17.0.9/lib/
HAMCREST = $(ABSOLUTE_PROJECT_DIR)/lib/hamcrest-core-1.3.jar
JSON = $(ABSOLUTE_PROJECT_DIR)/lib/json-20231013.jar

# Compile flags
JFLAGS = --module-path $(OPENJFX) --add-modules javafx.controls,javafx.fxml
# Classpath for JUNIT
JTESTCP = "$(JUNIT):$(HAMCREST):$(JSON):."
# Classpath for JSON
JCP = "$(JSON)"

# Source directory and testing directories
SRC_DIR = ./src
TEST_DIR = ./test

# Output directory
OUT_DIR = out

# Package name
PROGRAM = PantryPal
# Executable name
REALPROGRAM = PantryPal

# Define the main class
MAIN_CLASS = $(PROGRAM).$(REALPROGRAM)

# Find all .java files in the source directory
SRCS = $(wildcard $(SRC_DIR)/$(PROGRAM)/*.java)
TESTSRCS = $(wildcard $(TEST_DIR)/$(PROGRAM)/*.java)

# Generate .class file names from .java file names
CLASSES = $(patsubst $(SRC_DIR)/$(PROGRAM)/%.java, $(OUT_DIR)/$(PROGRAM)/%.class, $(SRCS))
TESTCLASSES = $(patsubst $(TEST_DIR)/$(PROGRAM)/%.java, $(OUT_DIR)/$(PROGRAM)/%.class, $(TESTSRCS))

# Convert Testclass names to executable path names (/ to .)
RUNNABLE_TEST_CLASSES = $(patsubst $(TEST_DIR)/$(PROGRAM)/%.java, $(PROGRAM).%, $(TESTSRCS))

# Filter test class executable names by ones which are actually tests
RUNNABLE_TESTS = $(filter %Test,$(RUNNABLE_TEST_CLASSES))

all: $(CLASSES) $(TESTCLASSES)
$(TESTCLASSES): $(SRCS) $(TESTSRCS)
	@mkdir -p $(@D)
	$(JAVAC) $(JFLAGS) -cp $(JTESTCP) -d $(OUT_DIR) $^

$(CLASSES): $(SRCS)
	@mkdir -p $(@D)
	$(JAVAC) $(JFLAGS) -cp $(JCP) -d $(OUT_DIR) $^

# Run the Java application
run: all
	$(JAVA) $(JFLAGS) -cp $(OUT_DIR) $(MAIN_CLASS)
test: all
	$(JAVA) $(JFLAGS) -cp $(JTESTCP):$(OUT_DIR) org.junit.runner.JUnitCore $(RUNNABLE_TESTS)

clean:
	rm -rf $(OUT_DIR)
