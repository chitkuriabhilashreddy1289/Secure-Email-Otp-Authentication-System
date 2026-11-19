import React,{useState,useEffect} from 'react';
import {createRoot} from 'react-dom/client';
import axios from 'axios';
import './style.css';
const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ||
        'http://localhost:9595/api'
});function App(){
 const [page,setPage]=useState('login'),[mode,setMode]=useState('login'),[email,setEmail]=useState(''),[name,setName]=useState(''),[code,setCode]=useState(''),[wait,setWait]=useState(0),[busy,setBusy]=useState(false),[message,setMessage]=useState(''),[profile,setProfile]=useState(null);
 useEffect(()=>{if(!wait)return;const timer=setTimeout(()=>setWait(w=>w-1),1000);return()=>clearTimeout(timer)},[wait]);
 async function run(action) {
    setBusy(true);
    setMessage('');

    try {
        await action();
    } catch (e) {
        console.error("Complete error:", e);
        console.error("Response:", e.response?.data);
        console.error("Status:", e.response?.status);

        setMessage(
            e.response?.data?.message ||
            e.message ||
            'Something went wrong'
        );
    } finally {
        setBusy(false);
    }
}
function send() {
    return run(async () => {
        const response = await api.post('/auth/request', {
            email: email.trim(),
            name: mode === 'register' ? name.trim() : ''
        });

        console.log("OTP Response:", response.data);

        setPage('otp');
        setCode('');
        setWait(60);
    });
}
 function verify(){return run(async()=>{const {data}=await api.post('/auth/verify',{email,code});const result=await api.get('/me',{headers:{Authorization:`Bearer ${data.token}`}});setProfile(result.data);setPage('dashboard')})}
 function logout(){setProfile(null);setCode('');setEmail('');setName('');setMessage('');setPage('login')}
 return <main className="layout"><section className="brand"><div className="logo">◈ SecureAuth</div><div className="pitch"><span className="tag">PASSWORDLESS BY DESIGN</span><h1>Security made<br/> beautifully simple.</h1><p>One email. One code. A secure way to access your account.</p><div className="features"><span>✓ One-time verification</span><span>✓ No passwords to remember</span></div></div><small>SECUREAUTH / IDENTITY PLATFORM</small></section><section className="content"><div className="panel">
 {page==='login'&&<><div className="symbol">✦</div><h2>{mode==='register'?'Create your account':'Welcome back'}</h2><p>Enter your email to receive a secure access code.</p><form onSubmit={e=>{e.preventDefault();send()}}>{mode==='register'&&<label>Full name<input required maxLength={100} value={name} onChange={e=>setName(e.target.value)} placeholder="Your name"/></label>}<label>Email address<input type="email" required maxLength={254} value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com"/></label><button disabled={busy}>{busy?'Sending…':'Send verification code →'}</button></form><div className="switch">{mode==='login'?'New here?':'Already a member?'} <button className="link" onClick={()=>{setMode(mode==='login'?'register':'login');setMessage('')}}>{mode==='login'?'Create account':'Sign in'}</button></div></>}
 {page==='otp'&&<><div className="symbol">✉</div><h2>Check your inbox</h2><p>We sent a six-digit code to <strong>{email}</strong>. It expires in five minutes.</p><form onSubmit={e=>{e.preventDefault();verify()}}><label>Verification code<input className="otp" autoFocus inputMode="numeric" autoComplete="one-time-code" pattern="[0-9]{6}" required maxLength={6} value={code} onChange={e=>setCode(e.target.value.replace(/\D/g,''))} placeholder="000000"/></label><button disabled={busy||code.length!==6}>{busy?'Verifying…':'Verify and continue →'}</button></form><div className="switch"><button className="link" disabled={busy||wait>0} onClick={send}>{wait?`Resend in ${wait}s`:'Resend code'}</button><button className="link" disabled={busy} onClick={()=>{setPage('login');setMessage('')}}>Change email</button></div></>}
 {page==='dashboard'&&profile&&<><div className="symbol">✓</div><span className="verified">EMAIL VERIFIED</span><h2>Hello, {profile.name}!</h2><p>You have successfully authenticated.</p><div className="profile"><small>ACCOUNT EMAIL</small><strong>{profile.email}</strong><small>STATUS</small><strong>Verified member</strong></div><button onClick={logout}>Sign out →</button></>}
 {message&&<div role="alert" className="error">{message}</div>}<footer>◈ Protected by SecureAuth</footer></div></section></main>
}
createRoot(document.getElementById('root')).render(<App/>);
