CP = -cp .:./packages/mail.jar

.PHONY: clean all run

all: MailIP.class

MailIP.class: MailIP.java
	javac $(CP) MailIP.java
	
run:
	java $(CP) MailIP

clean:
	rm *.class