
/** 메인 컴포넌트 */
class Colonization extends React.Component {
    ctx = '/';
    jwtToken = null;
    constructor(props) {
        this.ctx = props.ctx;
    }
    componentDidMount() {

    }
    render() {
		if(this.jwtToken == null) {
			return (<LoginScreen/>);
		}
        return (<MainScreen superInstance={this} />);
    }
}

class LoginScreen extends React.Component {
    render() {
        return (
            <div>
                <form onSubmit={() => {return false;}}>
                    <table class='layout full'>
                        <tr>
                            <th>ID</th>
                            <td><input type='text' name='id' class='full'/></td>
                        </tr>
						<tr>
                            <th>Password</th>
                            <td><input type='password' name='password' class='full'/></td>
                        </tr>
                    </table>
                </form>
            </div>
        );
    }
}

class MainScreen extends React.Component {
	render() {
		return (
			<div></div>
		);
	}
}