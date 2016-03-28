# Nate Documentation
TOML is used for _all_ configuration files. The configuration folder for
Nate is located in:

* Windows: `%APPDATA%/.nate-editor`
* *nix: `~/.nate-editor` 

The configuration file itself is `user_config.toml` in this directory.

## Settings

	property					type			default			unit		description
	font_face					string			Monospaced					the font face for the editor
	font_size					integer			14				px			the font size for the editor
	hungry_backspace 			boolean			false						backspace will eat tab_size amount of characters if possible
	tab_size					integer			4							size of a tab in characters
	match_braces				boolean			false						inserts a closing brace for curly { and square [ brackets
	anti_alias					boolean			true						smooth text rendering
	blink_cursor				boolean			false						blinks the cursor at the specified rate
	cursor_blink_latency		integer			1000			ms			the rate to blink the cursor at
	auto_save					boolean			true						will save at the specified rate
	save_rate					integer			1000			ms			the rate to save files
	highlight_current_line 	boolean			true						highlights the current line

## Syntax Highlighting
