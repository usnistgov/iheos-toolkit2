import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

def config = new CompilerConfiguration()
config.setTargetBytecode(CompilerConfiguration.JDK17)
config.setClasspathLoader(Thread.currentThread().getContextClassLoader())
return config
