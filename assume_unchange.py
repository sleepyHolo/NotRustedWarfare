# -*- coding: utf-8 -*-

"""
取消所有二进制文件的跟踪
"""

import os
import subprocess
from pathlib import Path

TARGET_EXTENSIONS = {".png", ".tmx", ".tsx"}
START_DIR = Path(__file__).parent
VERBOSE = True

def is_git_repo(path: Path) -> bool:
    return (path / ".git").exists()

def find_files_with_extensions(root: Path, extensions: set[str]) -> list[Path]:
    matched_files = []
    for dirpath, _, filenames in os.walk(root):
        for file in filenames:
            if Path(file).suffix.lower() in extensions:
                full_path = Path(dirpath) / file
                matched_files.append(full_path)
    return matched_files

def mark_assume_unchanged(file_path: Path):
    try:
        subprocess.run(
            ["git", "update-index", "--assume-unchanged", str(file_path)],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL
        )
        if VERBOSE:
            print(f"done: {file_path}")
    except subprocess.CalledProcessError:
        print(f"error: {file_path}")

def main():
    if not is_git_repo(START_DIR):
        print(".git not found.")
        return

    files = find_files_with_extensions(START_DIR, TARGET_EXTENSIONS)
    if not files:
        return
    
    for f in files:
        mark_assume_unchanged(f)

    print("\ndone.")

if __name__ == "__main__":
    main()
