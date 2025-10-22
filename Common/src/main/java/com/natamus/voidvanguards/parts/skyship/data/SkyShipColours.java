package com.natamus.voidvanguards.parts.skyship.data;

public class SkyShipColours {
	public static final int[][][] SHIP_COLORS = new int[][][]{
			// Level 0: Basic (3 colors)
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130}    // Medium gray engines
			},
			// Level 1: Basic+ (4 colors)
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130},   // Medium gray engines
					{80, 80, 90}       // Wing structure gray
			},
			// Level 2: Maximum pixels (5 colors)
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130},   // Medium gray engines
					{80, 80, 90},      // Wing structure
					{160, 160, 170}    // Light gray details
			},
			// Level 3: More colors (6 colors) - Added shading
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130},   // Medium gray engines
					{80, 80, 90},      // Wing structure
					{160, 160, 170},   // Light gray
					{100, 100, 110}    // Mid-dark gray shading
			},
			// Level 4: Even more colors (7 colors) - Complex shading
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130},   // Medium gray engines
					{80, 80, 90},      // Wing structure
					{160, 160, 170},   // Light gray
					{100, 100, 110},   // Mid-dark gray
					{200, 200, 210}    // Very light gray highlights
			},
			// Level 5: Maximum colors (8 colors) - Pilot included
			{
					{80, 160, 255},    // Blue cockpit
					{40, 40, 50},      // Dark gray body
					{120, 120, 130},   // Medium gray engines
					{80, 80, 90},      // Wing structure
					{160, 160, 170},   // Light gray
					{100, 100, 110},   // Mid-dark gray
					{200, 200, 210},   // Very light gray
					{255, 220, 180}    // Pilot (only non-gray)
			}
	};

	// Explosion colors (white and blue shades)
	public static final int[][][] EXPLOSION_COLORS = new int[][][]{
			// Frame 1 colors
			{
					{255, 255, 255}, // Bright white (center)
					{100, 150, 255}  // Bright blue
			},
			// Frame 2 colors
			{
					{255, 255, 255}, // Bright white
					{80, 130, 255}   // Medium blue
			},
			// Frame 3 colors
			{
					{255, 255, 255}, // Bright white
					{60, 110, 255}   // Darker blue
			},
			// Frame 4 colors
			{
					{200, 220, 255}, // Faded white-blue
					{40, 90, 255}    // Very dark blue
			}
	};
}