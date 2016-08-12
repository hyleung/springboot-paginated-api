var bs = ReactBootstrap;

var ListItem = React.createClass({
    render: function() {
        return (
                <bs.Row className="show-grid grid-data-row">
                    <bs.Col md={5}>{this.props.item.name}</bs.Col>
                    <bs.Col md={5} className="border-left">{this.props.item.description}</bs.Col>
                    <bs.Col md={1} className="border-left">
                        <ViewButton url={this.props.item._links["self"].href}/>
                    </bs.Col>
                    <bs.Col md={1} className="border-left">
                        <DeleteButton url={this.props.item._links["delete"].href} deleteHandler={this.props.refresh}/>
                    </bs.Col>
                </bs.Row>
               );
    }
});

var CreateButton = React.createClass({
    showForm: function(url) {
        $.ajax({
            url: url,
            dataType: 'json',
            headers: {
                accept: 'application/hal+json'
            },
            success: function(data) {
                console.log(data);
                this.setState( {
                    showModal: true,
                    data: data
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
            }
        });
    },
    getInitialState : function() {
        return {
            showModal: false,
            data: false
        };
    },
    close: function() {
       this.setState( {
           showModal: false
       });
    },
    render: function() {
        return (
                <div>
                <bs.Button onClick={this.showForm.bind(this, this.props.url)} bsStyle="primary" bsSize="xsmall" block>Create</bs.Button>
                <bs.Modal show={this.state.showModal} onHide={this.close}>
                    <bs.Modal.Header>
                        <bs.Modal.Title>New Item</bs.Modal.Title>
                    </bs.Modal.Header>
                    <bs.Modal.Body>
                       <CreateForm data={this.state.data} onSuccess={this.close}/>
                    </bs.Modal.Body>
                </bs.Modal>
                </div>
               );
    }
});

var CreateForm = React.createClass({
    getInitialState: function() {
        return {
            data: this.props.data
        };
    },
    updateField: function(e) {
        var entry = this.state.data
        entry[e.target.id] = e.target.value;
        this.setState({
            data: entry
        });
    },
    submit: function(url, onSuccess) {
        $.ajax({
            url: url,
            dataType: 'json',
            contentType: 'application/hal+json',
            method: 'POST',
            data: JSON.stringify(this.state.data),
            statusCode: {
                201: function(data) {
                    onSuccess();
                }
            },
            error: function(xhr, status, err) {
                if (xhr.status != 201) {
                    console.error(err.toString());
                }
            }
        });
    },
    render: function() {
        var obj = this.props.data;
        console.log(this.props)
        var result = [];
        for (var f in obj) {
            if (obj.hasOwnProperty(f) && !f.startsWith("_")) {
                result.push(
                        <div key={f}>
                        <bs.ControlLabel key={f+"label"}>{f}</bs.ControlLabel>
                        <bs.FormControl onChange={this.updateField} key={f} id={f} type="input" defaultValue={obj[f]}></bs.FormControl>
                        </div>
                        );
            }
        };
       return (
               <div>
                {result}
                <bs.Modal.Footer>
                    <bs.Button onClick={this.submit.bind(this, this.props.data._links["create"].href, this.props.onSuccess)} bsStyle="success">Submit</bs.Button>
                </bs.Modal.Footer>
                </div>
              );
    }
});

var DeleteButton = React.createClass({
    delete: function(url, handler) {
        console.log("Deleting " + url);
        $.ajax({
            url: url,
            method: 'DELETE',
            success: function(data) {
                handler();
            },
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
            }
        });
    },
    render: function() {
        return (
                <bs.Button onClick={this.delete.bind(this, this.props.url, this.props.deleteHandler)} bsStyle="danger" bsSize="xsmall" block>Delete</bs.Button>
               );
    }
});

var ViewButton = React.createClass({
    loadItem: function(url) {
        $.ajax({
            url: url,
            dataType: 'json',
            headers: {
                accept: 'application/hal+json'
            },
            success: function(data) {
                this.setState({data:data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState : function() {
        return {
            showModal: false,
            data: null
        }
    },
    close : function() {
        this.setState({showModal:false});
    },
    open : function() {
        this.setState({showModal:true});
        this.loadItem(this.props.url);
    },
    render: function() {
        var item = this.state.data;
        if (item != undefined) {
        return (<div>
                    <bs.Button onClick={this.open}  bsStyle="info" bsSize="xsmall" block>View</bs.Button>
                    <bs.Modal show={this.state.showModal} onHide={this.close}>
                            <bs.Modal.Header>
                                <bs.Modal.Title>{item.name}</bs.Modal.Title>
                            </bs.Modal.Header>
                            <bs.Modal.Body>
                            {item.description}
                            </bs.Modal.Body>
                            <bs.Modal.Footer>
                                <bs.Button onClick={this.close}>Close</bs.Button>
                            </bs.Modal.Footer>
                    </bs.Modal>
                </div>);
        }
        return (<div>
                    <bs.Button onClick={this.open}  bsStyle="info" bsSize="xsmall" block>View</bs.Button>
                </div>);
    },
});

var ListNavigation = React.createClass({
    navLinks: function () {
        var links = this.props.links;
        var relName = this.props.relName;
        var handleClick = function(href) {
            ReactDOM.render(
                    <List url={href}/>,
                    document.getElementById('content')
                    );
        }
        var result = [];
        if (links['next'] != undefined) {
            result.push(<bs.Pager.Item key='next' next onClick={handleClick.bind(this, links['next'].href)}>Next &rarr;</bs.Pager.Item>)
        }
        if (links['prev'] != undefined) {
            result.push(<bs.Pager.Item  key='previous' previous onClick={handleClick.bind(this, links['prev'].href)}>&larr; Previous</bs.Pager.Item>)
        }
        console.log(result)
        return result;
    },
    render: function() {
        return (
                <bs.Row className="show-grid grid-footer">
                    <bs.Col md={12}>
                        <bs.Pager className="grid-pager">
                        {this.navLinks()}
                        </bs.Pager>
                    </bs.Col>
                </bs.Row>
               );
        }
});

var List = React.createClass({
    loadList : function(url) {
        $.ajax({
            url: url,
            dataType: 'json',
            headers: {
                accept: 'application/hal+json'
            },
            success: function(data) {
                console.log(data);
                if (data._embedded != undefined) {
                    this.setState({data:data._embedded.items,
                    links:data._links});
                }
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState: function() {
        return {data:null,
        links:[]};
    },
    componentDidMount: function() {
        this.loadList(this.props.url);
    },
    componentWillReceiveProps: function(newProps) {
        console.log(newProps);
        this.loadList(newProps.url);
    },
    render: function() {
        if (this.state.data) {
            var refresh = this.loadList.bind(this, this.props.url);
            var listItems = this.state.data.map(function(item) {
            return (
                    <ListItem item={item} key={item.id} refresh={refresh}/>
                   );
        });
        return (
                <bs.Grid className="grid">
                    <bs.Row className="show-grid grid-header">
                        <bs.Col md={5}>Name</bs.Col>
                        <bs.Col md={5}>Description</bs.Col>
                        <bs.Col md={1}></bs.Col>
                        <bs.Col md={1}><CreateButton url={this.state.links["create-form"].href}/></bs.Col>
                    </bs.Row>
                    {listItems}
                    <ListNavigation links={this.state.links}/>
                </bs.Grid>
               );
        }
        return <div>Loading...</div>
    }
});

ReactDOM.render(
        <List url="http://localhost:8080/items?pageSize=4"/>,
        document.getElementById('content')
        );
