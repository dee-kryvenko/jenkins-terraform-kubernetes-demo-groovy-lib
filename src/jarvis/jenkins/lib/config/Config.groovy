package jarvis.jenkins.lib.config

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface Config {
    String resource()
    String type()
}
