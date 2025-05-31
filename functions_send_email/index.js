const functions = require('firebase-functions');
const nodemailer = require('nodemailer');

const GMAIL_USER = 'helpcapsular@gmail.com';
const GMAIL_PASS = 'nfpo irqi iqrb hthp';

const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: GMAIL_USER,
        pass: GMAIL_PASS
    }
});

exports.sendCapsuleEmail = functions.https.onRequest(async (req, res) => {
    const { toEmail, capsuleTitle, capsuleDate, capsuleLink } = req.body;

    if (!toEmail || !capsuleTitle || !capsuleDate || !capsuleLink) {
        return res.status(400).json({ success: false, message: 'Missing fields' });
    }

    const mailOptions = {
        from: `"Capsular App" <${GMAIL_USER}>`,
        to: toEmail,
        subject: `You’ve got a new capsule: "${capsuleTitle}"`,
        html: `
            <p>Hi there!</p>
            <p>You received a capsule titled: <strong>${capsuleTitle}</strong></p>
            <p>It will open on: <strong>${capsuleDate}</strong></p>
            <p>Click <a href="${capsuleLink}">here</a> to view it in the app.</p>
            <br/>
            <p>– Capsular Team</p>
        `
    };

    try {
        await transporter.sendMail(mailOptions);
        console.log(`✅ Email sent to ${toEmail}`);
        return res.status(200).json({ success: true, message: 'Email sent successfully' });
    } catch (error) {
        console.error('❌ Error sending email:', error);
        return res.status(500).json({ success: false, message: 'Failed to send email', error: error.toString() });
    }
});
