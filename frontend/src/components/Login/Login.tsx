const handleLogin = () => {
    console.log("Login clicked!");
    window.location.href = 'http://localhost:8080/login';
}

const Login = () => {
    return (
        <div>
            <button onClick={handleLogin}>Login to Spotify</button>
        </div>
    )
}

export default Login;