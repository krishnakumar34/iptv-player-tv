# 📱 Upload IPTV Player to GitHub via Termux

## Complete Step-by-Step Guide for Android/Termux

### Prerequisites

1. **Install Termux** from F-Droid (NOT Google Play Store)
   - Download: https://f-droid.org/en/packages/com.termux/
   
2. **Have a GitHub account**
   - Sign up at: https://github.com

---

## Step 1: Install Required Packages in Termux

Open Termux and run these commands:

```bash
# Update package lists
pkg update && pkg upgrade

# Install Git
pkg install git

# Install unzip (to extract the project)
pkg install unzip

# Optional: Install nano text editor
pkg install nano
```

---

## Step 2: Setup Git Configuration

Configure your Git identity:

```bash
# Set your GitHub username
git config --global user.name "YourGitHubUsername"

# Set your GitHub email
git config --global user.email "your.email@example.com"

# Set default branch name to 'main'
git config --global init.defaultBranch main
```

---

## Step 3: Transfer ZIP File to Termux

You have several options:

### Option A: Download Directly (if you have the link)
```bash
cd ~
wget YOUR_ZIP_FILE_URL -O iptv-player-complete.zip
```

### Option B: Copy from Downloads folder
```bash
# Give Termux storage permission first (it will ask)
termux-setup-storage

# Wait 5 seconds, then grant permission in the popup

# Copy from Downloads
cp ~/storage/downloads/iptv-player-complete.zip ~/
```

### Option C: Use Termux file picker
```bash
termux-setup-storage
# Then manually copy file using a file manager app
```

---

## Step 4: Extract the Project

```bash
# Navigate to home directory
cd ~

# Extract the ZIP file
unzip iptv-player-complete.zip

# Navigate into the project
cd iptv-player-tv

# Verify extraction
ls -la
```

You should see folders like `app`, `gradle`, and files like `build.gradle`.

---

## Step 5: Create GitHub Repository

### On GitHub Website (using phone browser):

1. Go to https://github.com/new
2. **Repository name**: `iptv-player` (or any name)
3. **Description**: "IPTV Player for Google TV with number pad feature"
4. **Public** or **Private** (your choice)
5. **DO NOT** check "Initialize with README"
6. Click **"Create repository"**

GitHub will show you a page with setup instructions. **Keep this page open!**

---

## Step 6: Generate GitHub Personal Access Token

GitHub no longer accepts passwords for command line. You need a token:

### On GitHub Website:

1. Go to https://github.com/settings/tokens
2. Click **"Generate new token"** → **"Generate new token (classic)"**
3. **Note**: "Termux IPTV Upload"
4. **Expiration**: Choose duration (e.g., 90 days)
5. **Select scopes**: Check **"repo"** (full control of private repositories)
6. Scroll down and click **"Generate token"**
7. **COPY THE TOKEN IMMEDIATELY** (you won't see it again!)
   - It looks like: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

---

## Step 7: Initialize Git and Push to GitHub

Back in Termux:

```bash
# Make sure you're in the project directory
cd ~/iptv-player-tv

# Initialize Git repository
git init

# Add all files
git add .

# Create first commit
git commit -m "Initial commit: IPTV Player with number pad feature"

# Add remote repository (replace with YOUR username)
git remote add origin https://github.com/YOUR_USERNAME/iptv-player.git

# Push to GitHub (will ask for credentials)
git push -u origin main
```

---

## Step 8: Enter GitHub Credentials

When prompted:

```
Username for 'https://github.com': YOUR_GITHUB_USERNAME
Password for 'https://YOUR_USERNAME@github.com': PASTE_YOUR_TOKEN_HERE
```

**Important:** 
- Username = Your GitHub username
- Password = The personal access token you generated (NOT your GitHub password)

---

## Step 9: Verify Upload

After successful push, you'll see:

```
Enumerating objects: 50, done.
Counting objects: 100% (50/50), done.
Delta compression using up to 8 threads
Compressing objects: 100% (45/45), done.
Writing objects: 100% (50/50), 45.23 KiB | 2.26 MiB/s, done.
Total 50 (delta 12), reused 0 (delta 0), pack-reused 0
To https://github.com/YOUR_USERNAME/iptv-player.git
 * [new branch]      main -> main
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

**Check on GitHub:**
1. Go to https://github.com/YOUR_USERNAME/iptv-player
2. You should see all your files!

---

## Step 10: GitHub Actions Auto-Build

### Wait for Auto-Build:

1. On GitHub, go to **"Actions"** tab
2. You'll see a workflow running (yellow dot)
3. Wait 5-10 minutes for build to complete (green checkmark)

### Download APK:

1. Click on the completed workflow
2. Scroll down to **"Artifacts"**
3. Download **"iptv-player-debug"**
4. Extract and install on Google TV!

---

## 🔄 Making Changes Later

If you want to update the code:

```bash
# Make your changes to files

# Check what changed
git status

# Add changes
git add .

# Commit with message
git commit -m "Added new feature"

# Push to GitHub
git push

# GitHub will auto-build again!
```

---

## 🆘 Troubleshooting

### "Permission denied (publickey)"
**Solution:** You're using SSH instead of HTTPS. Remove and re-add remote:
```bash
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/iptv-player.git
git push -u origin main
```

### "Authentication failed"
**Solution:** 
- Make sure you're using the TOKEN, not your password
- Generate a new token if it expired
- Check token has "repo" scope

### "Termux storage not accessible"
**Solution:**
```bash
termux-setup-storage
# Accept permission popup
# Try again after a few seconds
```

### "unzip command not found"
**Solution:**
```bash
pkg install unzip
```

### "Git push rejected"
**Solution:**
```bash
git pull origin main --rebase
git push -u origin main
```

---

## 💡 Pro Tips

### Save Token for Future Use
Create a credential helper:
```bash
# Store credentials (will save token)
git config --global credential.helper store

# Next time you push, enter token once
# It will be saved for future pushes
```

### View Git History
```bash
git log --oneline
```

### Create .gitignore (ignore build files)
```bash
cat > .gitignore << 'EOF'
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
*.apk
*.aab
EOF

git add .gitignore
git commit -m "Add gitignore"
git push
```

### Check Remote URL
```bash
git remote -v
```

---

## 📋 Quick Reference

```bash
# Clone repository
git clone https://github.com/USERNAME/iptv-player.git

# Check status
git status

# Add files
git add .

# Commit
git commit -m "Your message"

# Push
git push

# Pull latest
git pull

# View branches
git branch -a

# Create new branch
git checkout -b feature-name
```

---

## 🎯 Complete Example Session

```bash
# 1. Setup
pkg update
pkg install git unzip
git config --global user.name "JohnDoe"
git config --global user.email "john@example.com"

# 2. Extract project
cd ~
cp ~/storage/downloads/iptv-player-complete.zip ~/
unzip iptv-player-complete.zip
cd iptv-player-tv

# 3. Initialize and push
git init
git add .
git commit -m "Initial commit: IPTV Player v1.1.0"
git remote add origin https://github.com/JohnDoe/iptv-player.git
git push -u origin main

# Enter username: JohnDoe
# Enter password: ghp_your_token_here

# 4. Done! Check GitHub Actions tab for auto-build
```

---

## 🎉 Success!

Once uploaded:
- ✅ Your code is on GitHub
- ✅ GitHub Actions auto-builds APK
- ✅ Download APK from Artifacts
- ✅ Install on Google TV
- ✅ Any future changes auto-rebuild

---

**Your IPTV Player is now on GitHub with automatic APK building!** 🚀
