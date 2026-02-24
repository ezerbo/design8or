# Design8or Deployment Guide

This guide covers deploying Design8or to various cloud platforms at low or no cost.

## Table of Contents
- [Option 1: Render.com (FREE)](#option-1-rendercom-free)
- [Option 2: Railway.app ($5/month)](#option-2-railwayapp-5month)
- [Option 3: Fly.io (FREE with limits)](#option-3-flyio-free-with-limits)
- [Email Configuration](#email-configuration)
- [Environment Variables Reference](#environment-variables-reference)

---

## Option 1: Render.com (FREE) - Recommended for Beginners

### Pros
- ✅ Completely free tier (no credit card required)
- ✅ Automatic HTTPS
- ✅ Free PostgreSQL database
- ✅ One-click deployment from GitHub
- ✅ Automatic deploys on git push

### Cons
- ⚠️ Apps sleep after 15 min inactivity (30 sec wake-up time)
- ⚠️ Limited to 750 hours/month on free tier

### Deployment Steps

1. **Push code to GitHub**
   ```bash
   git add .
   git commit -m "Prepare for deployment"
   git push origin master
   ```

2. **Sign up at [Render.com](https://render.com)**
   - Use your GitHub account

3. **Create New Blueprint Instance**
   - Click "New +" → "Blueprint"
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml` and `Dockerfile`
   - Click "Apply"

   **Note**: The backend uses Docker to build and run the Java application

4. **Configure Environment Variables**
   
   Go to Backend service settings and add:
   ```
   ADMIN_USERNAME=your_admin_username
   ADMIN_PASSWORD=your_secure_password
   BACKEND_URL=https://design8or-backend.onrender.com
   EMAIL_FROM=noreply@yourdomain.com
   
   # Email settings (see Email Configuration below)
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-password
   ```

5. **Wait for deployment** (~5-10 minutes)

6. **Access your app**
   - Frontend: `https://design8or.onrender.com`
   - Backend API: `https://design8or-backend.onrender.com`
   - Swagger: `https://design8or-backend.onrender.com/swagger-ui.html`

### Upgrading from Free Tier

To avoid sleep on inactivity, upgrade backend to Starter ($7/month):
- Go to Backend service → Settings → Change Instance Type → Starter

---

## Option 2: Railway.app ($5/month) - Best Value

### Pros
- ✅ $5 free credit/month
- ✅ No sleep on inactivity
- ✅ Great performance
- ✅ Easy GitHub integration
- ✅ Built-in PostgreSQL

### Cons
- ⚠️ Requires credit card
- ⚠️ Costs after free credit (~$5-10/month)

### Deployment Steps

1. **Sign up at [Railway.app](https://railway.app)**

2. **Create New Project**
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your repository

3. **Add PostgreSQL Database**
   - Click "New" → "Database" → "PostgreSQL"

4. **Configure Backend Service**
   - Click on your service
   - Go to Variables tab and add:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=${{Postgres.DATABASE_URL}}
   DB_USERNAME=${{Postgres.PGUSER}}
   DB_PASSWORD=${{Postgres.PGPASSWORD}}
   FRONTEND_URL=https://your-frontend-url.railway.app
   BACKEND_URL=https://your-backend-url.railway.app
   ADMIN_USERNAME=your_admin
   ADMIN_PASSWORD=your_password
   
   # Email config
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-password
   EMAIL_FROM=noreply@yourdomain.com
   ```

5. **Generate Domain**
   - Settings → Networking → Generate Domain

6. **Deploy Frontend Separately** (optional)
   - Create new service from same repo
   - Set root directory to `/ui`
   - Build command: `npm install && npm run build`
   - Start command: `npx serve -s build -p $PORT`
   - Add variable: `REACT_APP_API_URL=https://your-backend-url.railway.app`

---

## Option 3: Fly.io (FREE with limits)

### Pros
- ✅ Free tier includes 3 shared VMs
- ✅ Global deployment
- ✅ No sleep on inactivity
- ✅ Good performance

### Cons
- ⚠️ More complex setup
- ⚠️ Command-line focused

### Deployment Steps

1. **Install Fly CLI**
   ```bash
   # macOS
   brew install flyctl
   
   # Linux/WSL
   curl -L https://fly.io/install.sh | sh
   ```

2. **Login**
   ```bash
   flyctl auth login
   ```

3. **Create fly.toml** (in project root)
   ```toml
   app = "design8or"
   
   [build]
     builder = "paketobuildpacks/builder:base"
     buildpacks = ["gcr.io/paketo-buildpacks/java"]
   
   [env]
     SPRING_PROFILES_ACTIVE = "prod"
     PORT = "8080"
   
   [[services]]
     internal_port = 8080
     protocol = "tcp"
   
     [[services.ports]]
       handlers = ["http"]
       port = 80
   
     [[services.ports]]
       handlers = ["tls", "http"]
       port = 443
   ```

4. **Create PostgreSQL Database**
   ```bash
   flyctl postgres create
   flyctl postgres attach design8or-db
   ```

5. **Set Environment Variables**
   ```bash
   flyctl secrets set ADMIN_PASSWORD=your_password
   flyctl secrets set MAIL_PASSWORD=your_email_password
   # ... other secrets
   ```

6. **Deploy**
   ```bash
   flyctl deploy
   ```

---

## Email Configuration

For sending designation emails, configure SMTP. Best free options:

### Gmail (Free - 500 emails/day)

1. **Enable 2-Factor Authentication** on your Google account

2. **Create App Password**
   - Go to Google Account → Security → 2-Step Verification
   - Scroll to "App passwords"
   - Generate password for "Mail"

3. **Configure Environment Variables**
   ```
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-16-char-app-password
   EMAIL_FROM=your-email@gmail.com
   ```

### SendGrid (Free - 100 emails/day)

1. **Sign up at [SendGrid](https://sendgrid.com)**

2. **Create API Key**
   - Settings → API Keys → Create API Key

3. **Configure Environment Variables**
   ```
   MAIL_HOST=smtp.sendgrid.net
   MAIL_PORT=587
   MAIL_USERNAME=apikey
   MAIL_PASSWORD=your-sendgrid-api-key
   EMAIL_FROM=noreply@yourdomain.com
   ```

### Mailgun (Free - 100 emails/day)

```
MAIL_HOST=smtp.mailgun.org
MAIL_PORT=587
MAIL_USERNAME=your-mailgun-username
MAIL_PASSWORD=your-mailgun-password
EMAIL_FROM=noreply@yourdomain.com
```

---

## Environment Variables Reference

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | `postgresql://user:pass@host:5432/db` |
| `DB_USERNAME` | Database username | `design8or` |
| `DB_PASSWORD` | Database password | `securepassword123` |
| `FRONTEND_URL` | Frontend URL (for CORS) | `https://design8or.onrender.com` |
| `BACKEND_URL` | Backend URL (for emails) | `https://api.design8or.com` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ADMIN_USERNAME` | Admin username | `design8tor` |
| `ADMIN_PASSWORD` | Admin password | `designator` |
| `MAIL_HOST` | SMTP host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP port | `587` |
| `MAIL_USERNAME` | Email username | - |
| `MAIL_PASSWORD` | Email password | - |
| `EMAIL_FROM` | From email address | `noreply@design8or.app` |
| `ROTATION_CRON` | Cron for auto-designation | `0 0 10 ? * *` |
| `STALE_CHECK_MINS` | Minutes before designation expires | `30` |

---

## Custom Domain (Optional)

### Render.com
1. Go to Service → Settings → Custom Domain
2. Add your domain
3. Update DNS records as instructed

### Railway.app
1. Service → Settings → Networking → Custom Domain
2. Add CNAME record pointing to Railway

### Fly.io
```bash
flyctl certs add yourdomain.com
```

Then update DNS:
```
A record: @ → [Fly IP from command output]
AAAA record: @ → [Fly IPv6 from command output]
```

---

## Monitoring & Logs

### Render.com
- Dashboard → Service → Logs (real-time)
- Metrics tab for CPU/Memory usage

### Railway.app
- Service → Observability tab
- Real-time logs and metrics

### Fly.io
```bash
flyctl logs
flyctl status
```

---

## Cost Comparison

| Platform | Free Tier | Paid Tier | Best For |
|----------|-----------|-----------|----------|
| **Render.com** | ✅ Full app + DB | $7/mo (no sleep) | Testing, demos |
| **Railway.app** | $5 credit/mo | ~$5-10/mo | Production (low traffic) |
| **Fly.io** | 3 VMs + Postgres | ~$2-5/mo | Global reach |
| **DigitalOcean** | ❌ None | $12/mo | Professional apps |
| **Heroku** | ❌ None | $7/mo | Established apps |

---

## Troubleshooting

### App won't start
- Check logs for errors
- Verify all environment variables are set
- Ensure PostgreSQL database is connected

### Database connection failed
- Verify `DATABASE_URL` format is correct
- Check firewall rules allow connections
- Ensure database exists and is running

### Email not sending
- Test SMTP credentials with online tools
- Check spam folder
- Verify MAIL_HOST and MAIL_PORT are correct
- Enable "Less secure app access" for Gmail (or use App Password)

### Push notifications not working
- Ensure app is served over HTTPS
- Check browser console for service worker errors
- Verify VAPID keys are set correctly
- Test notification permission is granted

---

## Next Steps After Deployment

1. **Test all features**
   - Create users
   - Send test designation
   - Verify emails received
   - Check push notifications work

2. **Set up monitoring**
   - Enable health checks
   - Set up uptime monitoring (UptimeRobot, StatusCake)

3. **Configure backups** (paid tiers)
   - Database backups
   - Regular snapshots

4. **Secure your app**
   - Change default admin password
   - Use strong database passwords
   - Enable 2FA where available

5. **Custom domain** (optional)
   - Register domain (Namecheap, Google Domains)
   - Configure DNS
   - Add SSL certificate (automatic on most platforms)
