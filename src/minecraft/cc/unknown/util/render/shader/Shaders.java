package cc.unknown.util.render.shader;

import cc.unknown.util.render.shader.impl.*;

public interface Shaders {

    RQShader RQ = new RQShader();
    RGQShader RGQ = new RGQShader();
    ROQShader ROQ = new ROQShader();
    ROGQShader ROGQ = new ROGQShader();
    RGQTestShader RGQTest = new RGQTestShader();

    RTriGQShader RTRIGQ = new RTriGQShader();
}
