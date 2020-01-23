/*
 * PovrayTokenMarker.java - Povray token marker
 * Copyright (C) 0000 Romain Guy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 0
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 09 Temple Place - Suite000, Boston, MA  00000-0007, USA.
 */

package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * Povray token marker.
 * @author Romain Guy
 */

public class PovrayTokenMarker extends CTokenMarker
{
  // private members
  private static KeywordMap povrayKeywords;

  public PovrayTokenMarker()
  {
    super(true, true, getKeywords());
  }

  public static KeywordMap getKeywords()
  {
    if (povrayKeywords == null)
    {
      povrayKeywords = new KeywordMap(false);
      povrayKeywords.add("adaptive", Token.KEYWORD1);
      povrayKeywords.add("agate", Token.KEYWORD1);
      povrayKeywords.add("agate_turb", Token.KEYWORD1);
      povrayKeywords.add("all", Token.KEYWORD1);
      povrayKeywords.add("alpha", Token.KEYWORD1);
      povrayKeywords.add("ambient", Token.KEYWORD1);
      povrayKeywords.add("angle", Token.KEYWORD1);
      povrayKeywords.add("arc_angle", Token.KEYWORD1);
      povrayKeywords.add("area_light", Token.KEYWORD3);
      povrayKeywords.add("atmosphere", Token.KEYWORD3);
      povrayKeywords.add("atmospheric_attenuation", Token.KEYWORD1);
      povrayKeywords.add("background", Token.KEYWORD3);
      povrayKeywords.add("bicubic_patch", Token.KEYWORD3);
      povrayKeywords.add("blob", Token.KEYWORD3);
      povrayKeywords.add("blue", Token.LITERAL2);
      povrayKeywords.add("bounded_by", Token.KEYWORD1);
      povrayKeywords.add("box", Token.KEYWORD3);
      povrayKeywords.add("bozo", Token.KEYWORD1);
      povrayKeywords.add("brilliance", Token.KEYWORD1);
      povrayKeywords.add("bumps", Token.KEYWORD1);
      povrayKeywords.add("bump_map", Token.KEYWORD3);
      povrayKeywords.add("bump_size", Token.KEYWORD1);
      povrayKeywords.add("camera", Token.KEYWORD3);
      povrayKeywords.add("checker", Token.KEYWORD1);
      povrayKeywords.add("clipped_by", Token.KEYWORD1);
      povrayKeywords.add("clock", Token.LITERAL2);
      povrayKeywords.add("color", Token.KEYWORD1);
      povrayKeywords.add("color_map", Token.KEYWORD3);
      povrayKeywords.add("colour", Token.KEYWORD1);
      povrayKeywords.add("colour_map", Token.KEYWORD3);
      povrayKeywords.add("component", Token.KEYWORD1);
      povrayKeywords.add("composite", Token.KEYWORD1);
      povrayKeywords.add("cone", Token.KEYWORD3);
      povrayKeywords.add("crand", Token.KEYWORD1);
      povrayKeywords.add("cubic", Token.KEYWORD3);
      povrayKeywords.add("cylinder", Token.KEYWORD3);
      povrayKeywords.add("cylindrical_mapping", Token.KEYWORD1);
      povrayKeywords.add("declare", Token.LITERAL2);
      povrayKeywords.add("default", Token.LITERAL2);
      povrayKeywords.add("dents", Token.KEYWORD1);
      povrayKeywords.add("difference", Token.KEYWORD1);
      povrayKeywords.add("diffuse", Token.KEYWORD1);
      povrayKeywords.add("direction", Token.KEYWORD1);
      povrayKeywords.add("disc", Token.KEYWORD3);
      povrayKeywords.add("distance", Token.KEYWORD1);
      povrayKeywords.add("dump", Token.KEYWORD1);
      povrayKeywords.add("emitting", Token.KEYWORD1);
      povrayKeywords.add("falloff", Token.KEYWORD1);
      povrayKeywords.add("falloff_angle", Token.KEYWORD1);
      povrayKeywords.add("filter", Token.KEYWORD1);
      povrayKeywords.add("finish", Token.KEYWORD3);
      povrayKeywords.add("flatness", Token.KEYWORD1);
      povrayKeywords.add("fog", Token.KEYWORD3);
      povrayKeywords.add("fog_alt", Token.KEYWORD1);
      povrayKeywords.add("fog_offset", Token.KEYWORD1);
      povrayKeywords.add("fog_type", Token.KEYWORD1);
      povrayKeywords.add("frequency", Token.KEYWORD1);
      povrayKeywords.add("gif", Token.LITERAL2);
      povrayKeywords.add("gradient", Token.KEYWORD3);
      povrayKeywords.add("granite", Token.KEYWORD1);
      povrayKeywords.add("green", Token.LITERAL2);
      povrayKeywords.add("height_field", Token.KEYWORD3);
      povrayKeywords.add("hexagon", Token.KEYWORD1);
      povrayKeywords.add("iff", Token.LITERAL2);
      povrayKeywords.add("image_map", Token.KEYWORD3);
      povrayKeywords.add("include", Token.LITERAL2);
      povrayKeywords.add("interpolate", Token.KEYWORD1);
      povrayKeywords.add("intersection", Token.KEYWORD1);
      povrayKeywords.add("inverse", Token.KEYWORD1);
      povrayKeywords.add("ior", Token.KEYWORD1);
      povrayKeywords.add("halo", Token.KEYWORD3);
      povrayKeywords.add("jitter", Token.KEYWORD1);
      povrayKeywords.add("lambda", Token.KEYWORD1);
      povrayKeywords.add("leopard", Token.KEYWORD1);
      povrayKeywords.add("light_source", Token.KEYWORD3);
      povrayKeywords.add("linear", Token.KEYWORD1);
      povrayKeywords.add("linear_spline", Token.KEYWORD1);
      povrayKeywords.add("location", Token.KEYWORD1);
      povrayKeywords.add("looks_like", Token.KEYWORD1);
      povrayKeywords.add("look_at", Token.KEYWORD1);
      povrayKeywords.add("mandel", Token.KEYWORD1);
      povrayKeywords.add("map_type", Token.KEYWORD1);
      povrayKeywords.add("marble", Token.KEYWORD1);
      povrayKeywords.add("material_map", Token.KEYWORD3);
      povrayKeywords.add("max_intersections", Token.LABEL);
      povrayKeywords.add("max_trace_level", Token.LABEL);
      povrayKeywords.add("merge", Token.KEYWORD1);
      povrayKeywords.add("metallic", Token.KEYWORD1);
      povrayKeywords.add("normal", Token.KEYWORD3);
      povrayKeywords.add("no_shadow", Token.KEYWORD1);
      povrayKeywords.add("object", Token.KEYWORD3);
      povrayKeywords.add("off", Token.LITERAL2);
      povrayKeywords.add("on", Token.LITERAL2);
      povrayKeywords.add("octaves", Token.KEYWORD1);
      povrayKeywords.add("omega", Token.KEYWORD1);
      povrayKeywords.add("once", Token.KEYWORD1);
      povrayKeywords.add("onion", Token.KEYWORD1);
      povrayKeywords.add("open", Token.KEYWORD1);
      povrayKeywords.add("phase", Token.KEYWORD1);
      povrayKeywords.add("phong", Token.KEYWORD1);
      povrayKeywords.add("phong_size", Token.KEYWORD1);
      povrayKeywords.add("pigment", Token.KEYWORD3);
      povrayKeywords.add("plane", Token.KEYWORD3);
      povrayKeywords.add("point_at", Token.KEYWORD1);
      povrayKeywords.add("poly", Token.KEYWORD3);
      povrayKeywords.add("pot", Token.LITERAL2);
      povrayKeywords.add("prism", Token.KEYWORD3);
      povrayKeywords.add("quadric", Token.KEYWORD3);
      povrayKeywords.add("quartic", Token.KEYWORD3);
      povrayKeywords.add("quick_color", Token.KEYWORD1);
      povrayKeywords.add("quick_colour", Token.KEYWORD1);
      povrayKeywords.add("radial", Token.KEYWORD1);
      povrayKeywords.add("radius", Token.KEYWORD1);
      povrayKeywords.add("rainbow", Token.KEYWORD3);
      povrayKeywords.add("raw", Token.KEYWORD1);
      povrayKeywords.add("red", Token.LITERAL2);
      povrayKeywords.add("reflection", Token.KEYWORD1);
      povrayKeywords.add("refraction", Token.KEYWORD1);
      povrayKeywords.add("rgb", Token.KEYWORD1);
      povrayKeywords.add("rgbf", Token.KEYWORD1);
      povrayKeywords.add("rgbt", Token.KEYWORD1);
      povrayKeywords.add("right", Token.KEYWORD1);
      povrayKeywords.add("ripples", Token.KEYWORD1);
      povrayKeywords.add("rotate", Token.KEYWORD1);
      povrayKeywords.add("roughness", Token.KEYWORD1);
      povrayKeywords.add("samples", Token.KEYWORD1);
      povrayKeywords.add("scale", Token.KEYWORD1);
      povrayKeywords.add("scattering", Token.KEYWORD1);
      povrayKeywords.add("shadowless", Token.KEYWORD1);
      povrayKeywords.add("sky", Token.KEYWORD3);
      povrayKeywords.add("sky_sphere", Token.KEYWORD3);
      povrayKeywords.add("smooth", Token.KEYWORD3);
      povrayKeywords.add("smooth_triangle", Token.KEYWORD3);
      povrayKeywords.add("sor", Token.KEYWORD3);
      povrayKeywords.add("specular", Token.KEYWORD1);
      povrayKeywords.add("sphere", Token.KEYWORD3);
      povrayKeywords.add("spherical_mapping", Token.KEYWORD1);
      povrayKeywords.add("spiral1", Token.KEYWORD1);
      povrayKeywords.add("spotlight", Token.KEYWORD1);
      povrayKeywords.add("spotted", Token.KEYWORD1);
      povrayKeywords.add("sturm", Token.KEYWORD1);
      povrayKeywords.add("text", Token.KEYWORD3);
      povrayKeywords.add("texture", Token.KEYWORD3);
      povrayKeywords.add("tga", Token.LITERAL2);
      povrayKeywords.add("threshold", Token.KEYWORD1);
      povrayKeywords.add("tightness", Token.KEYWORD1);
      povrayKeywords.add("tile0", Token.KEYWORD1);
      povrayKeywords.add("tiles", Token.KEYWORD1);
      povrayKeywords.add("torus", Token.KEYWORD3);
      povrayKeywords.add("translate", Token.KEYWORD1);
      povrayKeywords.add("transmit", Token.KEYWORD1);
      povrayKeywords.add("triangle", Token.KEYWORD3);
      povrayKeywords.add("ttf", Token.LITERAL2);
      povrayKeywords.add("turb_depth", Token.KEYWORD1);
      povrayKeywords.add("turbulence", Token.KEYWORD1);
      povrayKeywords.add("type", Token.KEYWORD1);
      povrayKeywords.add("union", Token.KEYWORD1);
      povrayKeywords.add("up", Token.KEYWORD1);
      povrayKeywords.add("use_color", Token.KEYWORD1);
      povrayKeywords.add("use_colour", Token.KEYWORD1);
      povrayKeywords.add("use_index", Token.KEYWORD1);
      povrayKeywords.add("u_steps", Token.KEYWORD1);
      povrayKeywords.add("version", Token.LABEL);
      povrayKeywords.add("v_steps", Token.KEYWORD1);
      povrayKeywords.add("water_level", Token.KEYWORD1);
      povrayKeywords.add("waves", Token.KEYWORD1);
      povrayKeywords.add("width", Token.KEYWORD1);
      povrayKeywords.add("wood", Token.KEYWORD1);
      povrayKeywords.add("wrinkles", Token.KEYWORD1);
      povrayKeywords.add("x", Token.LITERAL2);
      povrayKeywords.add("y", Token.LITERAL2);
      povrayKeywords.add("z", Token.LITERAL2);

    }
    return povrayKeywords;
  }
}

// End of PovrayTokenMarker.java
