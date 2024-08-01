var client_id = '0b30f0dcdb2e40dd9e173167b8c9f7f1';
var redirect_uri = 'http://localhost:3000/';

const handleLogin = () => {
    console.log("Login clicked!");

}

const Login = () => {
    return (
        <div>
            <button onClick={handleLogin}>Login to Spotify</button>
        </div>
    )
}

export default Login;