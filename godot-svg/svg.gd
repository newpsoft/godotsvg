"""
GodotSvg - A Godot Android plugin to read SVG files.
Copyright (C) 2021 Jonathan Pelletier, New Paradigm Software

This file is a part of free software with the following license:

Attribution 4.0 International (CC BY 4.0), https://creativecommons.org/licenses/by/4.0/legalcode
You are free to:
Share — copy and redistribute the material in any medium or format
Adapt — remix, transform, and build upon the material for any purpose, even commercially.
This license is acceptable for Free Cultural Works.
The licensor cannot revoke these freedoms as long as you follow the license terms.

"""
extends Node

class_name SVG, "res://icon.png"

var _singleton = null

# Called when the node enters the scene tree for the first time.
func _enter_tree():
	if(Engine.has_singleton("GodotSvg")):
		_singleton = Engine.get_singleton("GodotSvg")
	else:
		print("GodotSvg platform plugin is not found.  Make sure the singleton is selected.")

func file_to_png(file_path, width = 0, height = 0) -> Dictionary:
	""" Read an SVG file from local filesystem, and convert it to PNG as a raw byte array.

	If either width or height are 0 we first try to determine the image size from the SVG
	document declared size.  If the document also has no size declared we fallback to a small
	square.

	@param file_path Local file path.  A file URI will not work.
	@param width    Absolute pixel width we want to draw into.  A value of 0 will default to what
			is declared in the SVG document.
	@param height   Absolute pixel height we want to draw into.  A value of 0 will default to what
			is declared in the SVG document.
	@return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
	"""
	if _singleton:
		return _singleton.fileToPng(file_path, width, height)
	else:
		return { "success": false, "error": "The GodotSvg singleton does not exist or cannot be found." }

func resource_to_png(resource_id, width = 0, height = 0) -> Dictionary:
	""" Read an SVG raw resource, and convert it to PNG as a raw byte array.

	If either width or height are 0 we first try to determine the image size from the SVG
	document declared size.  If the document also has no size declared we fallback to a small
	square.

	@param resource_id Resource ID of the resource to read from, e.g. R.raw.filename_svg
	@param width      Absolute pixel width we want to draw into.  A value of 0 will default to what
			  is declared in the SVG document.
	@param height     Absolute pixel height we want to draw into.  A value of 0 will default to what
			  is declared in the SVG document.
	@return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
	"""
	if _singleton:
		return _singleton.resourceToPng(resource_id, width, height)
	else:
		return { "success": false, "error": "The GodotSvg singleton does not exist or cannot be found." }

func asset_to_png(asset_path, width = 0, height = 0) -> Dictionary:
	""" Read an SVG from packaged assets, and convert it to PNG as a raw byte array.

	If either width or height are 0 we first try to determine the image size from the SVG
	document declared size.  If the document also has no size declared we fallback to a small
	square.

	@param asset_path Asset path to the SVG file, e.g. "images/filename.svg"
	@param width     Absolute pixel width we want to draw into.  A value of 0 will default to what
			 is declared in the SVG document.
	@param height    Absolute pixel height we want to draw into.  A value of 0 will default to what
			 is declared in the SVG document.
	@return Map possible value types: {"success": Boolean, "value": byte[], "error": String}
	"""
	if _singleton:
		return _singleton.assetToPng(asset_path, width, height)
	else:
		return { "success": false, "error": "The GodotSvg singleton does not exist or cannot be found." }
