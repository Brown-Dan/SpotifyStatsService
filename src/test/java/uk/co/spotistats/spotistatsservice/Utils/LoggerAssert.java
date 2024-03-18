package uk.co.spotistats.spotistatsservice.Utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.mock.env.MockEnvironment;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerAssert implements BeforeEachCallback {

    private static final Logger ROOT = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final ConcurrentHashMap<String, Collection<ILoggingEvent>> LOGS = new ConcurrentHashMap<>();

    static {
        LogbackLoggingSystem logbackLoggingSystem = new LogbackLoggingSystem(LoggerAssert.class.getClassLoader());
        logbackLoggingSystem.beforeInitialize();
        logbackLoggingSystem.initialize(new LoggingInitializationContext(new MockEnvironment()), null, null);
    }

    private static final Appender<ILoggingEvent> APPENDER = new AppenderBase<>() {
        @Override
        protected void append(ILoggingEvent eventObject) {
            LOGS.putIfAbsent(eventObject.getThreadName(), new ConcurrentLinkedQueue<>());
            LOGS.get(eventObject.getThreadName()).add(eventObject);
        }

        @Override
        public void stop() {
            super.stop();
            this.start();
        }
    };

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        APPENDER.start();
        LOGS.put(Thread.currentThread().getName(), new ConcurrentLinkedQueue<>());
        ROOT.addAppender(APPENDER);
    }


    public void assertInfo(String content) {
        final Optional<ILoggingEvent> message = findMessage(Level.INFO, content);
        assertThat(message).isPresent();
    }

    public void assertError(String content, Throwable e) {
        final Optional<ILoggingEvent> message = findMessage(Level.ERROR, content);

        assertThat(message).isPresent();
        assertThat(message.get().getThrowableProxy().getClassName()).isEqualTo(e.getClass().getName());
    }

    public void assertError(String content) {
        final Optional<ILoggingEvent> message = findMessage(Level.ERROR, content);

        assertThat(message).isPresent();
    }

    private Optional<ILoggingEvent> findMessage(Level logLevel, String content) {
        return getLogs().stream()
                .filter(e -> e.getLevel() == logLevel && e.getFormattedMessage().equals(content))
                .findFirst();
    }

    private Collection<ILoggingEvent> getLogs() {
        return LOGS.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
