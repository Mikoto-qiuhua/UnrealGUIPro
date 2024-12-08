package org.qiuhua.UnrealGUIPro.JavaScripts;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.qiuhua.UnrealGUIPro.Main;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Scripts {

    private static final ConcurrentHashMap<Integer, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private static final ScriptEngine scriptEngine = getScriptEngine("-Dnahorn.args=--language=es6", "--global-per-engine");
    public static Object main(String script) throws ScriptException {

        final Map<String, Object> args = new HashMap<>();
//        Main.getMainPlugin().getLogger().info(script + " 的结果是 " + evalScript(script, args));
        return evalScript(script, args);
    }
    public static Object evalScript(String script, Map<String, Object> args) throws ScriptException{
        final CompiledScript compiledScript = compiledScripts.computeIfAbsent(script.hashCode(), key ->{
            try{
                return ((Compilable) scriptEngine).compile(script);
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
        });
        Object result = compiledScript.eval(new SimpleBindings(args));
        // 检查脚本结果是否为布尔值
        if (result instanceof Boolean) {
            return (boolean) result;
        } else {
            // 如果不是布尔值，则返回 true
            return true;
        }
    }


    public static ScriptEngine getScriptEngine(String... args){
        return new NashornScriptEngineFactory().getScriptEngine(args);
    }





}
