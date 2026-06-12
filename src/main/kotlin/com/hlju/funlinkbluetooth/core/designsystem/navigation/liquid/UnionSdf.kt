// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.hlju.funlinkbluetooth.core.designsystem.navigation.liquid

// Liquid Glass Union SDF — ports the union effect from the demo.html WGSL shader
// to AGSL, adding refraction, shape masking, and surface color blending.
// Both shapes are rounded-rect pills (sdRoundBox).

import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.blur.BackdropEffectScope
import top.yukonga.miuix.kmp.blur.runtimeShaderEffect
import top.yukonga.miuix.kmp.shader.isRuntimeShaderSupported

/**
 * Applies a liquid-glass union lens effect that smoothly merges two pill shapes
 * with an hourglass "neck" bridge.
 *
 * @param leftCenter     Center of the left pill shape (px, in backdrop-local coords).
 * @param leftHalfSize   Half-size (halfWidth, halfHeight) of the left pill (px).
 * @param leftRadius     Corner radius of the left pill (px). Set to halfHeight for a stadium.
 * @param rightCenter    Center of the right pill shape (px).
 * @param rightHalfSize  Half-size (halfWidth, halfHeight) of the right pill (px).
 * @param rightRadius    Corner radius of the right pill (px).
 * @param unionK         Blend radius controlling the neck width (px). 0 = no union.
 * @param refractionHeight  Height of the refraction zone inside the shape boundary (px).
 * @param refractionAmount  Magnitude of the refraction displacement (px).
 * @param depthEffect    Whether to add a radial depth component to the refraction gradient.
 * @param surfaceColor   Compose [Color] used for the translucent surface fill
 *                       inside the union shape. Rendered in SrcOver over the backdrop.
 */
fun BackdropEffectScope.unionLens(
    leftCenter: FloatArray,
    leftHalfSize: FloatArray,
    leftRadius: Float,
    rightCenter: FloatArray,
    rightHalfSize: FloatArray,
    rightRadius: Float,
    unionK: Float,
    refractionHeight: Float,
    refractionAmount: Float,
    depthEffect: Boolean = false,
    surfaceColor: Color,
) {
    if (!isRuntimeShaderSupported()) return
    if (unionK <= 0f && refractionHeight <= 0f && refractionAmount <= 0f) return

    val effectiveRefractionHeight = if (unionK > 0f) refractionHeight else 0f
    val effectiveRefractionAmount = if (unionK > 0f) refractionAmount else 0f

    if (padding < effectiveRefractionAmount) {
        padding = effectiveRefractionAmount
    }

    val scaleFactor = downscaleFactor.coerceAtLeast(1).toFloat()

    runtimeShaderEffect(
        key = "FunLinkLiquidGlassUnionLens",
        shaderString = UnionLensShader,
        uniformShaderName = "content",
    ) {
        setFloatUniform("size", size.width / scaleFactor, size.height / scaleFactor)
        setFloatUniform("offset", -padding / scaleFactor, -padding / scaleFactor)
        setFloatUniform(
            "leftCenter",
            leftCenter[0] / scaleFactor,
            leftCenter[1] / scaleFactor,
        )
        setFloatUniform(
            "leftHalfSize",
            leftHalfSize[0] / scaleFactor,
            leftHalfSize[1] / scaleFactor,
        )
        setFloatUniform("leftRadius", leftRadius / scaleFactor)
        setFloatUniform(
            "rightCenter",
            rightCenter[0] / scaleFactor,
            rightCenter[1] / scaleFactor,
        )
        setFloatUniform(
            "rightHalfSize",
            rightHalfSize[0] / scaleFactor,
            rightHalfSize[1] / scaleFactor,
        )
        setFloatUniform("rightRadius", rightRadius / scaleFactor)
        setFloatUniform("unionK", unionK / scaleFactor)
        setFloatUniform("refractionHeight", effectiveRefractionHeight / scaleFactor)
        setFloatUniform("refractionAmount", -effectiveRefractionAmount / scaleFactor)
        setFloatUniform("depthEffect", if (depthEffect) 1f else 0f)
        setFloatUniform(
            "surfaceColor",
            surfaceColor.red, surfaceColor.green, surfaceColor.blue, surfaceColor.alpha,
        )
    }
}

// ── AGSL shader ──────────────────────────────────────────────────────────────

private const val UnionLensShader = """
uniform shader content;

uniform float2 size;
uniform float2 offset;

uniform float2 leftCenter;
uniform float2 leftHalfSize;
uniform float  leftRadius;

uniform float2 rightCenter;
uniform float2 rightHalfSize;
uniform float  rightRadius;

uniform float  unionK;
uniform float  refractionHeight;
uniform float  refractionAmount;
uniform float  depthEffect;
uniform float4 surfaceColor;

// ── Helpers ──

float saturate_f(float x) { return clamp(x, 0.0, 1.0); }

float smoother(float x) {
    x = saturate_f(x);
    return x * x * x * (x * (x * 6.0 - 15.0) + 10.0);
}

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

float2 safeNormalize(float2 v) {
    float len = length(v);
    return len > 0.0001 ? v / len : float2(1.0, 0.0);
}

// ── SDF primitives ──

float sdRoundBox(float2 p, float2 c, float2 hs, float r) {
    float2 q = abs(p - c) - hs + float2(r);
    return length(max(q, float2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}

// ── Support functions for signed-gap computation ──

float supportRoundBox(float2 axis, float2 hs, float r) {
    float2 inner = max(hs - float2(r), float2(0.0));
    return dot(abs(axis), inner) + r;
}

float signedGapAlongAxis(
    float2 c1, float s1,
    float2 c2, float s2,
    float2 axis
) {
    return dot(c2 - c1, axis) - s1 - s2;
}

// ── Smooth / liquid-glass union ──

float smoothUnionSdf(float a, float b, float k) {
    k = max(k, 0.001);
    float h = saturate_f(0.5 + 0.5 * (b - a) / k);
    return mix(b, a, h) - k * h * (1.0 - h);
}

float liquidGlassUnion(float d1, float d2, float sg, float kIn) {
    float k = max(kIn, 0.001);
    float separation = max(sg, 0.0);
    float reach = smoother(1.0 - separation / max(k * 3.2, 1.0));
    float blendK = (k * 1.65 - separation * 0.18) * reach;
    return smoothUnionSdf(d1, d2, max(blendK, 0.001));
}

float hermite(float p0, float m0, float p1, float m1, float t, float span) {
    float t2 = t * t;
    float t3 = t2 * t;
    return
        (2.0 * t3 - 3.0 * t2 + 1.0) * p0 +
        (t3 - 2.0 * t2 + t) * span * m0 +
        (-2.0 * t3 + 3.0 * t2) * p1 +
        (t3 - t2) * span * m1;
}

float bridgeSDF(float2 p) {
    float2 axis = safeNormalize(rightCenter - leftCenter);
    float2 normal = float2(-axis.y, axis.x);

    // The visible liquid neck should be tangent to the facing stadium caps.
    // Model those caps as circles and connect the top/bottom contact points
    // with Hermite curves whose endpoint slopes match the circle tangents.
    float leftCapOffset = max(leftHalfSize.x - leftRadius, 0.0);
    float rightCapOffset = max(rightHalfSize.x - rightRadius, 0.0);
    float2 c1 = leftCenter + axis * leftCapOffset;
    float2 c2 = rightCenter - axis * rightCapOffset;
    float2 local = p - c1;
    float x = dot(local, axis);
    float y = dot(local, normal);
    float capDistance = max(dot(c2 - c1, axis), 1.0);

    float minRadius = max(min(leftRadius, rightRadius), 1.0);
    float strength = smoother(unionK / max(minRadius * 0.35, 1.0));
    float attachSin = mix(0.18, 0.58, strength);
    float attachCos = sqrt(max(1.0 - attachSin * attachSin, 0.0));
    float tangentSlope = attachCos / max(attachSin, 0.001);

    float x0 = leftRadius * attachCos;
    float x1 = capDistance - rightRadius * attachCos;
    float span = max(x1 - x0, 1.0);
    float t = saturate_f((x - x0) / span);
    float halfHeight = hermite(
        leftRadius * attachSin,
        -tangentSlope,
        rightRadius * attachSin,
        tangentSlope,
        t,
        span
    );

    float yDistance = abs(y) - halfHeight;
    float xDistance = max(x0 - x, x - x1);
    return max(yDistance, xDistance);
}

// ── Scene SDF (single evaluation point) ──

float sceneSDF(float2 p) {
    float d1 = sdRoundBox(p, leftCenter, leftHalfSize, leftRadius);
    float d2 = sdRoundBox(p, rightCenter, rightHalfSize, rightRadius);
    float2 axis = safeNormalize(rightCenter - leftCenter);
    float s1 = supportRoundBox(axis, leftHalfSize, leftRadius);
    float s2 = supportRoundBox(axis, rightHalfSize, rightRadius);
    float sg = signedGapAlongAxis(leftCenter, s1, rightCenter, s2, axis);
    return liquidGlassUnion(d1, d2, sg, unionK);
}

// ── Analytical gradient of the union SDF ──
// Uses the individual SDF gradients plus a linearized blend correction.
// Much cheaper than 4-point numerical differentiation.

float2 gradSdRoundBox(float2 p, float2 c, float2 hs, float r) {
    float2 q = abs(p - c) - hs + float2(r);
    if (q.x >= 0.0 || q.y >= 0.0) {
        return sign(p - c) * safeNormalize(max(q, float2(0.0)));
    } else {
        float gx = step(q.y, q.x);
        return sign(p - c) * float2(gx, 1.0 - gx);
    }
}

float2 gradSceneSDF(float2 p) {
    float d1 = sdRoundBox(p, leftCenter, leftHalfSize, leftRadius);
    float d2 = sdRoundBox(p, rightCenter, rightHalfSize, rightRadius);

    // Analytical gradients of the individual SDFs
    float2 g1 = gradSdRoundBox(p, leftCenter, leftHalfSize, leftRadius);
    float2 g2 = gradSdRoundBox(p, rightCenter, rightHalfSize, rightRadius);

    // Blend gradient using the same blend weights as smoothUnionSdf
    float k = max(unionK, 0.001);
    float2 axis = safeNormalize(rightCenter - leftCenter);
    float s1 = supportRoundBox(axis, leftHalfSize, leftRadius);
    float s2 = supportRoundBox(axis, rightHalfSize, rightRadius);
    float sg = signedGapAlongAxis(leftCenter, s1, rightCenter, s2, axis);
    float separation = max(sg, 0.0);
    float reach = smoother(1.0 - separation / max(k * 3.2, 1.0));
    float blendK = max((k * 1.65 - separation * 0.18) * reach, 0.001);

    float h = saturate_f(0.5 + 0.5 * (d2 - d1) / blendK);
    return mix(g2, g1, h);
}

// ── Main ──

half4 main(float2 coord) {
    float2 centered = coord + offset;
    float  d = bridgeSDF(centered);

    float aa = 0.5;

    float neckMask = 1.0 - smoothstep(-aa, aa, d);

    // Early-out: nothing to draw outside the neck
    if (neckMask < 0.001) return half4(0.0);

    // --- Refraction (based on union SDF) ---
    float2 refractedCoord = coord;
    if (d < 0.0 && refractionHeight > 0.0) {
        float2 grad = gradSceneSDF(centered);
        float  gLen = length(grad);
        float2 gNorm = gLen > 0.001 ? grad / gLen : float2(0.0, -1.0);

        if (depthEffect > 0.5) {
            float2 toCenter = safeNormalize(centered - (leftCenter + rightCenter) * 0.5);
            gNorm = safeNormalize(gNorm + toCenter * 0.15);
        }

        float inside = min(-d, refractionHeight);
        float refractD = circleMap(inside / refractionHeight) * (-refractionAmount);
        refractedCoord = coord + refractD * gNorm;
    }

    float4 color = float4(content.eval(refractedCoord));

    // --- Surface color fill (SrcOver over backdrop, inside neck only) ---
    color = float4(
        mix(color.rgb, surfaceColor.rgb, surfaceColor.a),
        color.a
    );

    // --- Apply neck mask ---
    color.a *= neckMask;

    return half4(color);
}
"""
