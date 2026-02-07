# 📝 Quick Command Reference - Termux to GitHub

## One-Time Setup

```bash
# Install packages
pkg update && pkg upgrade
pkg install git unzip

# Configure Git
git config --global user.name "YourName"
git config --global user.email "your@email.com"
git config --global init.defaultBranch main

# Setup storage access
termux-setup-storage
```

## Upload Project (First Time)

```bash
# 1. Copy ZIP to Termux
cp ~/storage/downloads/iptv-player-complete.zip ~/

# 2. Extract
cd ~
unzip iptv-player-complete.zip
cd iptv-player-tv

# 3. Initialize Git
git init
git add .
git commit -m "Initial commit"

# 4. Connect to GitHub (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/iptv-player.git

# 5. Push (will ask for username and TOKEN)
git push -u origin main
```

## GitHub Token

**Get Token:** https://github.com/settings/tokens
- Click "Generate new token (classic)"
- Check "repo" scope
- Copy token (starts with `ghp_`)
- Use as password when pushing

## Update Code Later

```bash
cd ~/iptv-player-tv
git add .
git commit -m "Updated feature"
git push
```

## Common Commands

```bash
git status              # See what changed
git log --oneline       # View history
git pull                # Get latest from GitHub
git remote -v           # Check remote URL
```

## Troubleshooting

**Authentication failed?**
→ Use TOKEN, not password

**Permission denied?**
→ Use HTTPS URL: `https://github.com/user/repo.git`

**Storage access denied?**
→ Run `termux-setup-storage` and accept popup

---

**That's it! GitHub will auto-build your APK after each push.** ✅
