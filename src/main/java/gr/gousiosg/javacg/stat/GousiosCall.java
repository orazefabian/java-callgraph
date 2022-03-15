package gr.gousiosg.javacg.stat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GousiosCall {

    private CallType callType;
    private MethodCallType methodCallType;

    private String classNameCaller;
    private String methodNameCaller;
    private String methodSignatureCaller;
    private List<String> methodParametersCaller;
    private String superClassCaller;
    private List<String> implementedInterfaces;

    private String classNameCallee;
    private String methodNameCallee;
    private String methodSignatureCallee;
    private List<String> methodParametersCallee;

    private boolean isClassInterface;

    private final String callComplete;

    public GousiosCall(String call) {
        this.callComplete = call;
        String[] callerAndCallee = call.split(" ");
        parseCallerString(callerAndCallee[0]);
        if (this.callType == CallType.M) {
            parseCalleeString(callerAndCallee[1]);
        }
    }

    private void parseCallerString(String s) {
        String[] parts = s.split(":");
        this.callType = CallType.valueOf(parts[0]);
        if (this.callType == CallType.M) {
            this.methodSignatureCaller = parts[2];
            String[] methodParts = parts[2].split("\\(");
            this.methodNameCaller = getClassNameWithout$(methodParts[0]);
            methodParts[1] = methodParts[1].replace(")", "");
            this.methodParametersCaller = Arrays.stream(methodParts[1].split(",")).map(this::getClassNameWithout$).collect(Collectors.toList());
            this.superClassCaller = getClassNameWithout$(parts[3]);
            this.implementedInterfaces = new ArrayList<>();
            this.classNameCaller = getClassNameWithout$(parts[1]);
            Arrays.stream(parts[4].substring(1, parts[4].length() - 1).split(",")).forEach(interf -> {
                if (!interf.equals("")) {
                    this.implementedInterfaces.add(getClassNameWithout$(interf));
                }
            });
            //check for constructor call
            if (this.methodNameCaller.equals("<init>")) {
                String[] simpleClassNameParts = this.classNameCaller.split("\\.");
                this.methodNameCaller = getClassNameWithout$(simpleClassNameParts[simpleClassNameParts.length - 1]);
                this.methodSignatureCaller = this.methodSignatureCaller.replace("<init>", this.methodNameCaller);
            }
            this.isClassInterface = Boolean.parseBoolean(parts[6]);
        } else {
            String[] subParts = this.callComplete.split(" ");
            String[] callerParts = subParts[0].split(":");
            this.classNameCaller = getClassNameWithout$(callerParts[1]);
            this.isClassInterface = Boolean.parseBoolean(callerParts[3]);
            this.classNameCallee = getClassNameWithout$(subParts[1]);
            this.implementedInterfaces = new ArrayList<>();
            Arrays.stream(callerParts[4].substring(1, callerParts[4].length() - 1).split(",")).forEach(interf -> {
                if (!interf.equals("")) {
                    this.implementedInterfaces.add(getClassNameWithout$(interf));
                }
            });

        }
    }

    private void parseCalleeString(String s) {
        String[] parts = s.split(":");
        this.methodSignatureCallee = parts[1];
        this.methodCallType = MethodCallType.valueOf(parts[0].substring(1, 2));
        this.classNameCallee = getClassNameWithout$(parts[0].split("\\)")[1]);
        String[] methodParts = parts[1].split("\\(");
        this.methodNameCallee = getClassNameWithout$(methodParts[0]);
        methodParts[1] = methodParts[1].replace(")", "");
        this.methodParametersCallee = Arrays.stream(methodParts[1].split(",")).map(this::getClassNameWithout$).collect(Collectors.toList());

        //check for constructor call
        if (this.methodNameCallee.equals("<init>")) {
            String[] simpleClassNameParts = this.classNameCallee.split("\\.");
            this.methodNameCallee = simpleClassNameParts[simpleClassNameParts.length - 1];
            this.methodSignatureCallee = this.methodSignatureCallee.replace("<init>", this.methodNameCallee);
        }
    }

    private String getClassNameWithout$(String className) {
        if (className.contains("$")) {
            String[] classParts = className.split("\\$");
            String[] packageParts = classParts[0].split("\\.");
            packageParts[packageParts.length - 1] = classParts[classParts.length - 1];
            String nameWithout$ = "";
            for (int i = 0; i < packageParts.length; i++) {
                nameWithout$ += packageParts[i];
                if (i != packageParts.length - 1) nameWithout$ += ".";
            }
            return nameWithout$;
        }
        return className;
    }

    public String getMethodSignatureCaller() {
        return methodSignatureCaller;
    }

    public String getMethodSignatureCallee() {
        return methodSignatureCallee;
    }

    public CallType getCallType() {
        return callType;
    }

    public MethodCallType getMethodCallType() {
        return methodCallType;
    }

    public String getClassNameCaller() {
        return classNameCaller;
    }

    public String getMethodNameCaller() {
        return methodNameCaller;
    }

    public List<String> getMethodParametersCaller() {
        return methodParametersCaller;
    }

    public String getClassNameCallee() {
        return classNameCallee;
    }

    public String getMethodNameCallee() {
        return methodNameCallee;
    }

    public List<String> getMethodParametersCallee() {
        return methodParametersCallee;
    }

    public String getSuperClassCaller() {
        return superClassCaller;
    }

    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    public boolean isClassInterface() {
        return isClassInterface;
    }

    @Override
    public String toString() {
        return this.callComplete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.callComplete);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        GousiosCall temp = (GousiosCall) obj;
        if (temp.callType == CallType.C && this.callType == CallType.C) {
            return this.classNameCaller.equals(temp.classNameCaller);
        }
        if (this.getClassNameCaller().equals(temp.getClassNameCaller()) && this.methodNameCaller.equals(temp.methodNameCaller)) {
            return true;
        } else {
            return this.getClassNameCallee().equals(temp.getClassNameCallee()) && this.methodNameCallee.equals(temp.methodNameCallee);
        }
    }

    public enum CallType {
        M, //method type
        C //class type
    }

    public enum MethodCallType {
        M, //virtual method call
        I, //interface call
        O, //special call
        S, //static call
        D, //dynamic call
    }
}


